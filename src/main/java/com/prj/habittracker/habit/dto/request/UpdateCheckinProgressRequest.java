package com.prj.habittracker.habit.dto.request;

import java.math.BigDecimal;

public record UpdateCheckinProgressRequest(
        BigDecimal progress
) {
}
