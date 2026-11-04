package com.prj.habittracker.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank @Size(max = 100) String username,
        @Size(max = 2048) String avatarUrl,
        @NotBlank String timezone
) {}
