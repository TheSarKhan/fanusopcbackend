package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;

public record StatRequest(int statValue, @NotBlank String suffix, @NotBlank String label, @NotBlank String subLabel, int displayOrder) {}
