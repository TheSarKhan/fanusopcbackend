package com.fanus.dto;

import java.util.List;

public record ReportsDto(
    HeadlineMetric conversion,
    HeadlineMetric completion,
    HeadlineMetric averageRating,
    HeadlineMetric activeUsers,
    List<FunnelStep> funnel,
    List<TrafficSource> trafficSources,
    List<List<Integer>> hourlyHeatmap,           // 7 rows (Mon..Sun) × 24 cols, intensity 0..6
    List<TopConvertingArticle> topConverting,
    List<PsychologistPerformance> performance
) {
    public record HeadlineMetric(double value, String unit, double deltaAbs, String deltaUnit, String label) {}
    public record FunnelStep(String label, long count, double pctOfTotal, String color) {}
    public record TrafficSource(String label, int percent, String color) {}
    public record TopConvertingArticle(String title, long views, long requests, double conversionRate) {}
    public record PsychologistPerformance(String initials, String avatarColor, String name, long sessions, int completionPct, double rating) {}
}
