package com.fanus.service;

import com.fanus.dto.AvailableSlotDto;
import com.fanus.dto.TimeSlotDto;
import com.fanus.dto.TimeSlotOverrideDto;
import com.fanus.dto.TimeSlotOverrideRequest;
import com.fanus.dto.TimeSlotRequest;
import com.fanus.entity.Appointment;
import com.fanus.entity.Psychologist;
import com.fanus.entity.PsychologistTimeSlot;
import com.fanus.entity.PsychologistTimeSlotOverride;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.AppointmentRepository;
import com.fanus.repository.PsychologistRepository;
import com.fanus.repository.PsychologistTimeSlotOverrideRepository;
import com.fanus.repository.PsychologistTimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PsychologistTimeSlotService {

    private final PsychologistTimeSlotRepository slotRepository;
    private final PsychologistTimeSlotOverrideRepository overrideRepository;
    private final PsychologistRepository psychologistRepository;
    private final AppointmentRepository appointmentRepository;

    // ─── Recurring slots CRUD ────────────────────────────────────────────────

    public List<TimeSlotDto> listSlots(Long psychologistId) {
        return slotRepository.findByPsychologistIdOrderByDayOfWeekAscStartTimeAsc(psychologistId)
            .stream().map(this::toSlotDto).toList();
    }

    @Transactional
    public TimeSlotDto createSlot(Long psychologistId, TimeSlotRequest req) {
        Psychologist p = requirePsychologist(psychologistId);
        validateTimeRange(req.startTime(), req.endTime());
        PsychologistTimeSlot slot = PsychologistTimeSlot.builder()
            .psychologist(p)
            .dayOfWeek(req.dayOfWeek())
            .startTime(req.startTime())
            .endTime(req.endTime())
            .active(req.active() == null ? true : req.active())
            .build();
        return toSlotDto(slotRepository.save(slot));
    }

    @Transactional
    public TimeSlotDto updateSlot(Long psychologistId, Long slotId, TimeSlotRequest req) {
        PsychologistTimeSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new ResourceNotFoundException("Time slot not found: " + slotId));
        if (!slot.getPsychologist().getId().equals(psychologistId)) {
            throw new ResourceNotFoundException("Time slot not found for this psychologist");
        }
        validateTimeRange(req.startTime(), req.endTime());
        slot.setDayOfWeek(req.dayOfWeek());
        slot.setStartTime(req.startTime());
        slot.setEndTime(req.endTime());
        if (req.active() != null) slot.setActive(req.active());
        return toSlotDto(slotRepository.save(slot));
    }

    @Transactional
    public void deleteSlot(Long psychologistId, Long slotId) {
        PsychologistTimeSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new ResourceNotFoundException("Time slot not found: " + slotId));
        if (!slot.getPsychologist().getId().equals(psychologistId)) {
            throw new ResourceNotFoundException("Time slot not found for this psychologist");
        }
        slotRepository.delete(slot);
    }

    // ─── Overrides CRUD ──────────────────────────────────────────────────────

    public List<TimeSlotOverrideDto> listOverrides(Long psychologistId) {
        return overrideRepository.findByPsychologistIdOrderByOverrideDateDescStartTimeAsc(psychologistId)
            .stream().map(this::toOverrideDto).toList();
    }

    @Transactional
    public TimeSlotOverrideDto createOverride(Long psychologistId, TimeSlotOverrideRequest req) {
        Psychologist p = requirePsychologist(psychologistId);
        if (!"BLOCK".equals(req.overrideType()) && !"EXTRA".equals(req.overrideType())) {
            throw new IllegalArgumentException("overrideType must be BLOCK or EXTRA");
        }
        if ("EXTRA".equals(req.overrideType())) {
            if (req.startTime() == null || req.endTime() == null) {
                throw new IllegalArgumentException("EXTRA override requires startTime and endTime");
            }
            validateTimeRange(req.startTime(), req.endTime());
        }
        PsychologistTimeSlotOverride o = PsychologistTimeSlotOverride.builder()
            .psychologist(p)
            .overrideDate(req.overrideDate())
            .overrideType(req.overrideType())
            .startTime(req.startTime())
            .endTime(req.endTime())
            .note(req.note())
            .build();
        return toOverrideDto(overrideRepository.save(o));
    }

    @Transactional
    public void deleteOverride(Long psychologistId, Long overrideId) {
        PsychologistTimeSlotOverride o = overrideRepository.findById(overrideId)
            .orElseThrow(() -> new ResourceNotFoundException("Override not found: " + overrideId));
        if (!o.getPsychologist().getId().equals(psychologistId)) {
            throw new ResourceNotFoundException("Override not found for this psychologist");
        }
        overrideRepository.delete(o);
    }

    // ─── Availability computation ────────────────────────────────────────────

    /**
     * Compute concrete bookable slots for the given psychologist between [from, to].
     * Splits each availability window into chunks of `defaultSessionMinutes`,
     * subtracts blocked dates and existing ASSIGNED/CONFIRMED appointments,
     * and skips slots that are already in the past.
     */
    public List<AvailableSlotDto> availability(Long psychologistId, LocalDate from, LocalDate to) {
        Psychologist p = requirePsychologist(psychologistId);
        if (from == null) from = LocalDate.now();
        if (to == null) to = from.plusDays(14);
        if (to.isBefore(from)) to = from;

        int sessionMin = p.getDefaultSessionMinutes() > 0 ? p.getDefaultSessionMinutes() : 50;

        List<PsychologistTimeSlot> weekly = slotRepository
            .findByPsychologistIdAndActiveTrueOrderByDayOfWeekAscStartTimeAsc(psychologistId);

        List<PsychologistTimeSlotOverride> overrides = overrideRepository
            .findByPsychologistIdAndOverrideDateBetweenOrderByOverrideDateAscStartTimeAsc(psychologistId, from, to);

        Map<LocalDate, List<PsychologistTimeSlotOverride>> overridesByDate = overrides.stream()
            .collect(Collectors.groupingBy(PsychologistTimeSlotOverride::getOverrideDate));

        // Existing bookings in the range to subtract from availability
        LocalDateTime rangeStart = from.atStartOfDay();
        LocalDateTime rangeEnd = to.plusDays(1).atStartOfDay();
        List<Appointment> bookings = appointmentRepository.findActiveBookingsInRange(psychologistId, rangeStart, rangeEnd);

        List<AvailableSlotDto> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            List<PsychologistTimeSlotOverride> dayOverrides = overridesByDate.getOrDefault(date, List.of());
            boolean fullBlock = dayOverrides.stream()
                .anyMatch(o -> "BLOCK".equals(o.getOverrideType())
                    && o.getStartTime() == null && o.getEndTime() == null);
            if (fullBlock) continue;

            // Build candidate windows: weekly recurring + EXTRA overrides for this date
            List<TimeWindow> windows = new ArrayList<>();
            int isoDow = toIso(date.getDayOfWeek());
            for (PsychologistTimeSlot s : weekly) {
                if (s.getDayOfWeek() == isoDow) windows.add(new TimeWindow(s.getStartTime(), s.getEndTime()));
            }
            for (PsychologistTimeSlotOverride o : dayOverrides) {
                if ("EXTRA".equals(o.getOverrideType())) {
                    windows.add(new TimeWindow(o.getStartTime(), o.getEndTime()));
                }
            }
            if (windows.isEmpty()) continue;

            // Apply partial BLOCK overrides: cut blocked time out of windows
            for (PsychologistTimeSlotOverride o : dayOverrides) {
                if (!"BLOCK".equals(o.getOverrideType())) continue;
                if (o.getStartTime() == null || o.getEndTime() == null) continue;
                windows = subtract(windows, o.getStartTime(), o.getEndTime());
            }

            for (TimeWindow w : windows) {
                LocalDateTime cursor = LocalDateTime.of(date, w.start);
                LocalDateTime end = LocalDateTime.of(date, w.end);
                while (!cursor.plusMinutes(sessionMin).isAfter(end)) {
                    LocalDateTime slotEnd = cursor.plusMinutes(sessionMin);
                    if (!cursor.isBefore(now) && !overlapsBooking(bookings, cursor, slotEnd)) {
                        result.add(new AvailableSlotDto(cursor, slotEnd));
                    }
                    cursor = slotEnd;
                }
            }
        }
        return result;
    }

    private boolean overlapsBooking(List<Appointment> bookings, LocalDateTime start, LocalDateTime end) {
        for (Appointment a : bookings) {
            if (a.getStartAt() == null || a.getEndAt() == null) continue;
            if (a.getStartAt().isBefore(end) && a.getEndAt().isAfter(start)) return true;
        }
        return false;
    }

    private static List<TimeWindow> subtract(List<TimeWindow> windows, LocalTime cutStart, LocalTime cutEnd) {
        List<TimeWindow> out = new ArrayList<>();
        for (TimeWindow w : windows) {
            if (!cutStart.isBefore(w.end) || !cutEnd.isAfter(w.start)) {
                out.add(w);
                continue;
            }
            if (cutStart.isAfter(w.start)) out.add(new TimeWindow(w.start, cutStart));
            if (cutEnd.isBefore(w.end)) out.add(new TimeWindow(cutEnd, w.end));
        }
        return out;
    }

    private static int toIso(DayOfWeek dow) {
        return dow.getValue(); // Java's DayOfWeek already uses ISO 1..7 (Mon..Sun)
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private Psychologist requirePsychologist(Long id) {
        return psychologistRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Psychologist not found: " + id));
    }

    private static void validateTimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
    }

    private TimeSlotDto toSlotDto(PsychologistTimeSlot s) {
        return new TimeSlotDto(s.getId(), s.getPsychologist().getId(),
            s.getDayOfWeek(), s.getStartTime(), s.getEndTime(), s.isActive());
    }

    private TimeSlotOverrideDto toOverrideDto(PsychologistTimeSlotOverride o) {
        return new TimeSlotOverrideDto(o.getId(), o.getPsychologist().getId(),
            o.getOverrideDate(), o.getOverrideType(),
            o.getStartTime(), o.getEndTime(), o.getNote());
    }

    private record TimeWindow(LocalTime start, LocalTime end) {}
}
