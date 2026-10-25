package com.prj.habittracker.user.dto.response;

import com.prj.habittracker.rpg.character.dto.response.PenaltySettlementResponse;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        PenaltySettlementResponse penalty
) {}