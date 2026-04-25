package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;

public record FaqRequest(@NotBlank String question, @NotBlank String answer, int displayOrder, boolean active) {}
