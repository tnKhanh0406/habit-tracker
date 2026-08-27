package com.prj.habittracker.habit.service;

import com.prj.habittracker.habit.entity.HabitEntity;
import org.springframework.stereotype.Service;

@Service
public class HabitStreakService {
    public void updateStreaks(HabitEntity habit, boolean oldCompleted, boolean newCompleted) {
        Integer currentStreak = habit.getCurrentStreak();
        if (!oldCompleted && newCompleted) {
            currentStreak++;
        } else if (oldCompleted && !newCompleted) {
            currentStreak = Math.max(0, currentStreak - 1);
        }
        habit.setCurrentStreak(currentStreak);
        if (currentStreak > habit.getLongestStreak()) {
            habit.setLongestStreak(currentStreak);
        }
    }
}
