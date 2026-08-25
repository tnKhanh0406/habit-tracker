package com.prj.habittracker.category.dto.request;

import java.util.UUID;

public record UpdateCategoryRequest(
        UUID id,
        String name
) {
}
