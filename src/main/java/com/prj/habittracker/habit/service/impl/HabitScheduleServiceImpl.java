package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.habit.entity.HabitScheduleEntity;
import com.prj.habittracker.habit.service.HabitScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class HabitScheduleServiceImpl implements HabitScheduleService {
    @Override
    public boolean isHabitScheduled(HabitScheduleEntity schedule, LocalDate date) {
        if (date.isBefore(schedule.getStartDate())) {
            return false;
        }
        if (schedule.getEndDate() != null && date.isAfter(schedule.getEndDate())) {
            return false;
        }
        return switch (schedule.getScheduleType()) {
            case DAILY -> isDaily(schedule, date);
            case WEEKLY -> isWeekly(schedule, date);
            case MONTHLY -> isMonthly(schedule, date);
            case YEARLY -> isYearly(schedule, date);
        };
    }

    private boolean isDaily(HabitScheduleEntity schedule, LocalDate date) {
        long days = ChronoUnit.DAYS.between(schedule.getStartDate(), date);
        return days % schedule.getIntervalValue() == 0;
    }

    private boolean isWeekly(HabitScheduleEntity schedule, LocalDate date) {
        long weeks = ChronoUnit.WEEKS.between(schedule.getStartDate(), date);
        if (weeks % schedule.getIntervalValue() != 0) {
            return false;
        }
        return schedule.getWeekdays().stream()
                .anyMatch(day -> day.getWeekday() == date.getDayOfWeek().getValue() % 7);
    }

    private boolean isMonthly(HabitScheduleEntity schedule, LocalDate date) {
        long months = ChronoUnit.MONTHS.between(schedule.getStartDate(), date);
        if (months % schedule.getIntervalValue() != 0) {
            return false;
        }
        YearMonth currentMonth = YearMonth.from(date);

        int targetDay = Math.min(schedule.getStartDate().getDayOfMonth(), currentMonth.lengthOfMonth());
        return date.getDayOfMonth() == targetDay;
    }

    private boolean isYearly(HabitScheduleEntity schedule, LocalDate date) {
        long years = ChronoUnit.YEARS.between(schedule.getStartDate(), date);
        if (years % schedule.getIntervalValue() != 0) {
            return false;
        }
        int targetMonth = schedule.getStartDate().getMonthValue();
        int targetDay = Math.min(schedule.getStartDate().getDayOfMonth(), date.lengthOfMonth());
        if (date.getMonthValue() != targetMonth) {
            return false;
        }
        return date.getDayOfMonth() == targetDay;
    }
}
