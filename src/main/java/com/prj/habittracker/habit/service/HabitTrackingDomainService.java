package com.prj.habittracker.habit.service;

import com.prj.habittracker.common.enums.TrackingType;
import com.prj.habittracker.common.enums.CompletionSource;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class HabitTrackingDomainService {
    public void validateTarget(TrackingType trackingType, BigDecimal targetValue, String targetUnit) {
        if (trackingType == TrackingType.QUANTITY) {
            if (targetValue == null || targetValue.signum() <= 0) {
                throw new IllegalArgumentException("Target value must be greater than 0 for quantity habits");
            }
            if (targetUnit == null || targetUnit.isBlank()) {
                throw new IllegalArgumentException("Target unit is required for quantity habits");
            }
        }
    }

    public boolean reachedTarget(HabitEntity habit, BigDecimal progress) {
        return habit.getTrackingType() == TrackingType.QUANTITY
                && habit.getTarget() != null
                && progress != null
                && progress.compareTo(habit.getTarget().getValue()) >= 0;
    }

    public void applyProgressCompletion(HabitEntity habit, HabitCheckinEntity checkin) {
        boolean completed = reachedTarget(habit, checkin.getProgress());
        checkin.setCompleted(completed);
        checkin.setCompletionSource(completed ? CompletionSource.PROGRESS : null);
    }

    public void reconcileCheckin(HabitEntity habit, HabitCheckinEntity checkin, TrackingType previousTrackingType) {
        if (checkin.getCompletionSource() == CompletionSource.MANUAL) {
            checkin.setCompleted(true);
            return;
        }
        if (previousTrackingType == TrackingType.QUANTITY && habit.getTrackingType() == TrackingType.CHECK) {
            checkin.setCompleted(false);
            checkin.setCompletionSource(null);
            return;
        }
        applyProgressCompletion(habit, checkin);
    }

    public void requireTrackingType(HabitEntity habit, TrackingType expected) {
        if (habit.getTrackingType() != expected) {
            throw new IllegalArgumentException("Habit tracking type must be " + expected);
        }
    }
}