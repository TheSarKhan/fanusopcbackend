package com.fanus.dto;

public record FaqDto(Long id, String question, String answer, int displayOrder, boolean active) {}
