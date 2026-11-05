package com.prj.habittracker.party.dto.response;

import com.prj.habittracker.common.enums.PartyRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PartyMemberResponse(
        UUID userId,
        String username,
        String avatarUrl,
        PartyRole role,
        OffsetDateTime joinedAt
) {}
