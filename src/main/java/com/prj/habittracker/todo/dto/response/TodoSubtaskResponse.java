package com.prj.habittracker.todo.dto.response;

import java.util.UUID;

public record TodoSubtaskResponse(
        UUID id,
        String title,
        Boolean completed,
        Integer position
) {
}
