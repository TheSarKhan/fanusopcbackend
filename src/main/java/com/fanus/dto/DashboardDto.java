package com.fanus.dto;

import java.util.List;
import java.util.Map;

public record DashboardDto(
    StatBlock totalUsers,
    StatBlock activePsychologists,
    StatBlock pendingAppointments,
    StatBlock newMessages,
    StatBlock articleReads,
    List<DailyFlow> appointmentFlow,
    List<ActivityEntry> recentActivity,
    List<TopArticle> topArticles,
    List<TopicSlice> topicDistribution,
    Map<String, String> systemStatus
) {
    public record StatBlock(long value, Long secondary, Double deltaPercent, String label) {}

    public record DailyFlow(String date, long confirmed, long pending, long cancelled) {}

    public record ActivityEntry(
        String type,        // appointment, blog_post, announcement, psychologist, payment_failed
        String tone,        // sage, gold, rose, lilac, ox
        String title,       // bold/inline html-safe text — frontend renders b tags
        String meta,        // "2 dəq əvvəl · …"
        String at           // ISO timestamp
    ) {}

    public record TopArticle(int rank, String title, String author, String category, long views, Double deltaPct) {}

    public record TopicSlice(String label, int percent, String color) {}
}
