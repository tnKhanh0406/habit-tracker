package com.prj.habittracker.todo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubtaskRequest(
    UUID id,
    @NotBlank
    String title,
    @NotNull
    Boolean completed,
    @NotNull @Min(0)
    Integer position
) {
}
