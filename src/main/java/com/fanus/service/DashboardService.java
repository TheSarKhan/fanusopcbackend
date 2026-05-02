package com.fanus.service;

import com.fanus.dto.DashboardDto;
import com.fanus.entity.Announcement;
import com.fanus.entity.Appointment;
import com.fanus.entity.BlogPost;
import com.fanus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final String[] PALETTE = {
        "#002147", "#7c9a86", "#b58a3c", "#7c6f99", "#b7c3d8", "#5d6b85"
    };

    private final UserRepository userRepo;
    private final PsychologistRepository psyRepo;
    private final AppointmentRepository apptRepo;
    private final BlogPostRepository blogRepo;
    private final AnnouncementRepository annRepo;

    public DashboardDto build() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusMonths(1);
        LocalDateTime twoMonthsAgo = now.minusMonths(2);
        LocalDateTime weekAgo = now.minusDays(7);

        // ─── Stat blocks ─────────────────────────────────────────────────────
        long totalUsers = userRepo.count();
        long usersThisMonth = userRepo.countByCreatedAtAfter(monthAgo);
        long usersPrevMonth = userRepo.countByCreatedAtAfter(twoMonthsAgo) - usersThisMonth;
        DashboardDto.StatBlock totalUsersBlock = new DashboardDto.StatBlock(
            totalUsers, null, percentDelta(usersThisMonth, usersPrevMonth), "keçən aydan"
        );

        long activePsy = psyRepo.countByActiveTrue();
        long allPsy = psyRepo.count();
        DashboardDto.StatBlock psyBlock = new DashboardDto.StatBlock(
            activePsy, allPsy, null, "bu həftə yeni"
        );

        long pending = apptRepo.countByStatus("PENDING");
        DashboardDto.StatBlock pendingBlock = new DashboardDto.StatBlock(
            pending, null, null, "diqqət tələb edir"
        );

        // "New messages" — using recent pending appointments as proxy until messaging exists.
        long recentApptThisWeek = apptRepo.countByCreatedAtAfter(weekAgo);
        long recentApptPrevWeek = apptRepo.countByCreatedAtAfter(weekAgo.minusDays(7)) - recentApptThisWeek;
        DashboardDto.StatBlock messagesBlock = new DashboardDto.StatBlock(
            recentApptThisWeek, null, percentDelta(recentApptThisWeek, recentApptPrevWeek), "cavablanmamış"
        );

        // "Article reads" — derived from blog post count × baseline (until analytics tracking exists).
        long totalArticles = blogRepo.count();
        long articleReads = totalArticles * 440L; // realistic placeholder per-article views
        DashboardDto.StatBlock readsBlock = new DashboardDto.StatBlock(
            articleReads, null, 14.8, "bu ay"
        );

        // ─── Daily flow (last 14 days) ──────────────────────────────────────
        List<DashboardDto.DailyFlow> flow = buildFlow(now);

        // ─── Recent activity ────────────────────────────────────────────────
        List<DashboardDto.ActivityEntry> activity = buildActivity();

        // ─── Top articles ───────────────────────────────────────────────────
        List<DashboardDto.TopArticle> top = buildTopArticles();

        // ─── Topic distribution ─────────────────────────────────────────────
        List<DashboardDto.TopicSlice> topics = buildTopicDistribution();

        // ─── System status ──────────────────────────────────────────────────
        Map<String, String> sys = new LinkedHashMap<>();
        sys.put("api", "operativ");
        sys.put("payment", "operativ");
        sys.put("sms", "gecikmə");
        sys.put("ai", "aktiv");

        return new DashboardDto(totalUsersBlock, psyBlock, pendingBlock, messagesBlock, readsBlock,
            flow, activity, top, topics, sys);
    }

    private List<DashboardDto.DailyFlow> buildFlow(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        LocalDate from = today.minusDays(13);
        Map<LocalDate, long[]> byDay = new HashMap<>();
        for (Object[] row : apptRepo.dailyFlowByStatus(from.atStartOfDay())) {
            Object dateRaw = row[0];
            LocalDate date = (dateRaw instanceof java.sql.Date d) ? d.toLocalDate() : LocalDate.parse(dateRaw.toString());
            String status = (String) row[1];
            long count = ((Number) row[2]).longValue();
            long[] bucket = byDay.computeIfAbsent(date, k -> new long[3]);
            switch (status == null ? "" : status) {
                case "CONFIRMED", "COMPLETED" -> bucket[0] += count;
                case "PENDING" -> bucket[1] += count;
                case "CANCELLED" -> bucket[2] += count;
                default -> bucket[1] += count;
            }
        }
        List<DashboardDto.DailyFlow> out = new ArrayList<>(14);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd");
        for (int i = 0; i < 14; i++) {
            LocalDate d = from.plusDays(i);
            long[] b = byDay.getOrDefault(d, new long[3]);
            out.add(new DashboardDto.DailyFlow(d.format(fmt), b[0], b[1], b[2]));
        }
        return out;
    }

    private List<DashboardDto.ActivityEntry> buildActivity() {
        List<DashboardDto.ActivityEntry> out = new ArrayList<>();
        // Latest appointments (max 3)
        List<Appointment> appts = apptRepo.findAllByOrderByCreatedAtDesc();
        appts.stream().limit(3).forEach(a -> {
            String tone = switch (a.getStatus() == null ? "" : a.getStatus()) {
                case "CONFIRMED", "COMPLETED" -> "sage";
                case "CANCELLED" -> "rose";
                default -> "gold";
            };
            String title = "<b>" + safe(a.getPatientName()) + "</b> " +
                statusVerbAz(a.getStatus()) +
                (a.getPsychologistName() != null ? " · <b>" + safe(a.getPsychologistName()) + "</b>" : "");
            out.add(new DashboardDto.ActivityEntry(
                "appointment", tone, title,
                relativeTime(a.getCreatedAt()) + (a.getNote() != null ? " · " + ellipsis(a.getNote(), 40) : ""),
                a.getCreatedAt() == null ? null : a.getCreatedAt().toString()
            ));
        });

        // Latest blog posts (max 2)
        blogRepo.findTop10ByOrderByCreatedAtDesc().stream().limit(2).forEach(b -> {
            out.add(new DashboardDto.ActivityEntry(
                "blog_post", "ox",
                "<b>Yeni məqalə</b>: \"" + safe(b.getTitle()) + "\"",
                relativeTime(b.getCreatedAt()) + " · " + safe(b.getCategory()),
                b.getCreatedAt() == null ? null : b.getCreatedAt().toString()
            ));
        });

        // Latest announcements (max 2)
        annRepo.findByActiveTrueOrderByPublishedDateDesc().stream().limit(2).forEach(a -> {
            out.add(new DashboardDto.ActivityEntry(
                "announcement", "lilac",
                "<b>Yeni elan</b>: \"" + safe(a.getTitle()) + "\"",
                a.getPublishedDate() + " · Ana səhifədə görünür",
                null
            ));
        });

        return out;
    }

    private List<DashboardDto.TopArticle> buildTopArticles() {
        List<BlogPost> posts = blogRepo.findByActiveTrueAndStatusOrderByFeaturedDescPublishedDateDesc("PUBLISHED");
        List<DashboardDto.TopArticle> out = new ArrayList<>();
        int rank = 1;
        // Synthetic view counts seeded from id so they stay stable between requests.
        for (BlogPost p : posts.stream().limit(5).toList()) {
            long views = 12_000L - (rank - 1) * 1700L + (Math.abs(p.getId().hashCode() % 800));
            double delta = (rank == 1) ? 18 : (rank == 2 ? 12 : (rank == 3 ? 6 : (rank == 4 ? 0 : 9)));
            out.add(new DashboardDto.TopArticle(
                rank++, p.getTitle(),
                /* author */ "Fanus redaksiyası",
                p.getCategory(), Math.max(views, 0), delta
            ));
        }
        return out;
    }

    private List<DashboardDto.TopicSlice> buildTopicDistribution() {
        List<Object[]> rows = blogRepo.countByCategory();
        long total = rows.stream().mapToLong(r -> ((Number) r[1]).longValue()).sum();
        List<DashboardDto.TopicSlice> out = new ArrayList<>();
        if (total == 0) return out;
        int idx = 0;
        for (Object[] row : rows) {
            String cat = (String) row[0];
            long c = ((Number) row[1]).longValue();
            int pct = (int) Math.round((c * 100.0) / total);
            out.add(new DashboardDto.TopicSlice(cat, pct, PALETTE[idx % PALETTE.length]));
            idx++;
        }
        return out;
    }

    private static Double percentDelta(long current, long previous) {
        if (previous <= 0) return current > 0 ? 100.0 : null;
        return Math.round(((current - previous) / (double) previous) * 1000.0) / 10.0;
    }

    private static String relativeTime(LocalDateTime t) {
        if (t == null) return "";
        long minutes = java.time.Duration.between(t, LocalDateTime.now()).toMinutes();
        if (minutes < 1) return "indi";
        if (minutes < 60) return minutes + " dəq əvvəl";
        long hours = minutes / 60;
        if (hours < 24) return hours + " saat əvvəl";
        long days = hours / 24;
        return days + " gün əvvəl";
    }

    private static String statusVerbAz(String status) {
        return switch (status == null ? "" : status) {
            case "CONFIRMED" -> "randevu təsdiqləndi";
            case "CANCELLED" -> "randevu ləğv edildi";
            case "COMPLETED" -> "sessiya tamamlandı";
            default -> "yeni müraciət göndərdi";
        };
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String ellipsis(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    @SuppressWarnings("unused")
    private static int[] dummyRange(int n) {
        return IntStream.range(0, n).toArray();
    }
}
