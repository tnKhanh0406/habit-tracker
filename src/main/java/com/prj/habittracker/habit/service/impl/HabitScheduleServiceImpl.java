package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.common.enums.ScheduleType;
import com.prj.habittracker.habit.dto.request.HabitScheduleRequest;
import com.prj.habittracker.habit.dto.response.HabitScheduleResponse;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.entity.HabitScheduleEntity;
import com.prj.habittracker.habit.entity.HabitScheduleWeekdayEntity;
import com.prj.habittracker.habit.repository.HabitScheduleRepository;
import com.prj.habittracker.habit.service.HabitScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HabitScheduleServiceImpl implements HabitScheduleService {

    private final HabitScheduleRepository habitScheduleRepository;

    @Override
    @Transactional
    public void createHabitSchedule(HabitEntity habit, HabitScheduleRequest request) {
        HabitScheduleEntity schedule = new HabitScheduleEntity();
        schedule.setHabit(habit);
        schedule.setScheduleType(request.scheduleType());
        schedule.setStartDate(request.startDate());
        schedule.setEndDate(request.endDate());
        schedule.setIntervalValue(request.intervalValue());
        if (request.scheduleType() == ScheduleType.WEEKLY) {
            List<HabitScheduleWeekdayEntity> weekdays = request.weekdays().stream()
                    .map(day -> {
                        HabitScheduleWeekdayEntity weekday = new HabitScheduleWeekdayEntity();
                        weekday.setHabitSchedule(schedule);
                        weekday.setWeekday(day);
                        return weekday;
                    }).toList();
            schedule.setWeekdays(weekdays);
        }
        habit.setSchedule(schedule);
    }

    @Override
    @Transactional
    public boolean updateHabitSchedule(UUID habitId, HabitScheduleRequest request) {
        HabitScheduleEntity schedule = habitScheduleRepository.findByHabitId(habitId)
                .orElseThrow(EntityNotFoundException::new);
        validateScheduleRequest(request);
        schedule.setScheduleType(request.scheduleType());
        schedule.setIntervalValue(request.intervalValue());
        schedule.setStartDate(request.startDate());
        schedule.setEndDate(request.endDate());

        if (request.scheduleType() == ScheduleType.WEEKLY) {
            List<HabitScheduleWeekdayEntity> weekdays = request.weekdays().stream()
                    .map(day -> {
                        HabitScheduleWeekdayEntity weekday = new HabitScheduleWeekdayEntity();
                        weekday.setHabitSchedule(schedule);
                        weekday.setWeekday(day);
                        return weekday;
                    }).toList();
            schedule.getWeekdays().clear();
            schedule.getWeekdays().addAll(weekdays);
        }
        return isHabitScheduled(schedule, LocalDate.now());
    }

    @Override
    public HabitScheduleResponse getHabitSchedule(UUID habitId) {
        HabitScheduleEntity schedule = habitScheduleRepository.findByHabitId(habitId)
                .orElseThrow(() -> new EntityNotFoundException("Habit schedule not found for habitId: " + habitId));
        return new HabitScheduleResponse(
                schedule.getId(),
                schedule.getScheduleType(),
                schedule.getIntervalValue(),
                schedule.getStartDate(),
                schedule.getEndDate() != null ? schedule.getEndDate() : null,
                schedule.getWeekdays().stream()
                        .map(HabitScheduleWeekdayEntity::getWeekday)
                        .toList()
        );
    }

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
        long days = ChronoUnit.DAYS.between(
                schedule.getStartDate(),
                date
        );
        return days % schedule.getIntervalValue() == 0;
    }

    private boolean isWeekly(HabitScheduleEntity schedule, LocalDate date) {
        LocalDate startWeek = schedule.getStartDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate currentWeek = date
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        long weeks = ChronoUnit.WEEKS.between(startWeek, currentWeek);

        if (weeks % schedule.getIntervalValue() != 0) {
            return false;
        }

        int weekday = date.getDayOfWeek().getValue() - 1;

        return schedule.getWeekdays().stream()
                .anyMatch(day -> day.getWeekday() == weekday);
    }

    private boolean isMonthly(HabitScheduleEntity schedule, LocalDate date) {
        YearMonth startMonth = YearMonth.from(schedule.getStartDate());
        YearMonth currentMonth = YearMonth.from(date);

        long months = ChronoUnit.MONTHS.between(startMonth, currentMonth);

        if (months % schedule.getIntervalValue() != 0) {
            return false;
        }

        int targetDay = Math.min(schedule.getStartDate().getDayOfMonth(), currentMonth.lengthOfMonth());
        return date.getDayOfMonth() == targetDay;
    }

    private boolean isYearly(HabitScheduleEntity schedule, LocalDate date) {
        int years = date.getYear() - schedule.getStartDate().getYear();
        if (years % schedule.getIntervalValue() != 0) {
            return false;
        }
        int targetMonth = schedule.getStartDate().getMonthValue();
        YearMonth targetYearMonth = YearMonth.of(date.getYear(), targetMonth);

        int targetDay = Math.min(
                schedule.getStartDate().getDayOfMonth(),
                targetYearMonth.lengthOfMonth()
        );

        return date.getMonthValue() == targetMonth && date.getDayOfMonth() == targetDay;
    }

    private void validateScheduleRequest(HabitScheduleRequest request) {
        if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        if (request.scheduleType() == ScheduleType.WEEKLY) {
            if (request.weekdays() == null || request.weekdays().isEmpty()) {
                throw new IllegalArgumentException("Weekdays must be provided for weekly schedule");
            }
            boolean invalid = request.weekdays().stream()
                    .anyMatch(day -> day == null || day < 0 || day > 6);
            if (invalid) {
                throw new IllegalArgumentException("Weekdays must be between 0 and 6");
            }
        }

        if (request.intervalValue() == null || request.intervalValue() <= 0) {
            throw new IllegalArgumentException("Interval value must be greater than 0");
        }
    }
}
