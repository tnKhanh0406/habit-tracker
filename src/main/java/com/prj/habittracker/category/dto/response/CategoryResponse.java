package com.prj.habittracker.category.dto.response;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name
) {
}
