package com.prj.habittracker.habit.service;

import com.prj.habittracker.common.enums.TrackingType;
import com.prj.habittracker.common.enums.CompletionSource;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.entity.HabitTargetEntity;
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

    public boolean reachedTarget(TrackingType trackingType,
                                 HabitTargetEntity target,
                                 BigDecimal progress) {
        return trackingType == TrackingType.QUANTITY
                && target != null
                && progress != null
                && progress.compareTo(target.getValue()) >= 0;
    }

    public void applyProgressCompletion(HabitEntity habit,
                                        HabitTargetEntity target,
                                        HabitCheckinEntity checkin) {
        if (checkin.getCompletionSource() == CompletionSource.MANUAL) {
            checkin.setCompleted(true);
            return;
        }
        boolean completed = reachedTarget(habit.getTrackingType(), target, checkin.getProgress());
        checkin.setCompleted(completed);
        checkin.setCompletionSource(completed ? CompletionSource.PROGRESS : null);
    }

    public void reconcileCheckin(HabitEntity habit,
                                 HabitTargetEntity target,
                                 HabitCheckinEntity checkin,
                                 TrackingType previousTrackingType) {
        if (checkin.getCompletionSource() == CompletionSource.MANUAL) {
            checkin.setCompleted(true);
            return;
        }
        if (previousTrackingType == TrackingType.QUANTITY && habit.getTrackingType() == TrackingType.CHECK) {
            checkin.setCompleted(false);
            checkin.setCompletionSource(null);
            return;
        }
        applyProgressCompletion(habit, target, checkin);
    }

    public void requireTrackingType(HabitEntity habit, TrackingType expected) {
        if (habit.getTrackingType() != expected) {
            throw new IllegalArgumentException("Habit tracking type must be " + expected);
        }
    }
}