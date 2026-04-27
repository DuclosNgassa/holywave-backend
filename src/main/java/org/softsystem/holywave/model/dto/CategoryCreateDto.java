package org.softsystem.holywave.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateDto(@NotBlank String name) {
}
