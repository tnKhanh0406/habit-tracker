package com.prj.habittracker.user.dto.response;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}