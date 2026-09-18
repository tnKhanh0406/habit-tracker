package com.prj.habittracker.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$")
        String username,

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Size(min = 12, max = 72)
        String password
) {}
