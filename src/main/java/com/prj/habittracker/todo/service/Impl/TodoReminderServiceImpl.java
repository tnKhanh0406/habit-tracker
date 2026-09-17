package com.prj.habittracker.todo.service.Impl;

import com.prj.habittracker.todo.dto.request.TodoReminderRequest;
import com.prj.habittracker.todo.dto.response.TodoReminderResponse;
import com.prj.habittracker.todo.entity.TodoEntity;
import com.prj.habittracker.todo.entity.TodoReminderEntity;
import com.prj.habittracker.todo.repository.TodoReminderRepository;
import com.prj.habittracker.todo.repository.TodoRepository;
import com.prj.habittracker.todo.service.TodoReminderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoReminderServiceImpl implements TodoReminderService {

    private final TodoReminderRepository todoReminderRepository;
    private final TodoRepository todoRepository;

    @Override
    public List<TodoReminderResponse> getTodoReminders(UUID todoId) {
        List<TodoReminderEntity> reminders = todoReminderRepository.findAllByTodo_Id(todoId);
        return reminders.stream()
                .map(reminder -> new TodoReminderResponse(
                        reminder.getId(),
                        reminder.getReminderTime(),
                        reminder.getEnabled()
                ))
                .toList();
    }

    @Override
    public void updateTodoReminders(UUID todoId, List<TodoReminderRequest> requests) {
        List<TodoReminderEntity> reminders = todoReminderRepository.findAllByTodo_Id(todoId);
        TodoEntity todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));
        Map<UUID, TodoReminderEntity> reminderMap = reminders.stream()
                .collect(Collectors.toMap(TodoReminderEntity::getId, r -> r));
        Set<UUID> requestIds = new HashSet<>();
        List<TodoReminderEntity> result = new ArrayList<>();
        for (TodoReminderRequest request : requests) {
            if (request.id() != null) {
                requestIds.add(request.id());
                TodoReminderEntity reminder = reminderMap.get(request.id());
                if (reminder == null) {
                    throw new EntityNotFoundException("Todo reminder not found");
                }
                reminder.setReminderTime(request.reminderTime());
                reminder.setEnabled(request.enabled());
                result.add(reminder);
            } else {
                TodoReminderEntity newReminder = new TodoReminderEntity();
                newReminder.setTodo(todo);
                newReminder.setReminderTime(request.reminderTime());
                newReminder.setEnabled(request.enabled());
                result.add(newReminder);
            }
        }
        List<TodoReminderEntity> deletedReminders = reminders.stream()
                .filter(reminder -> reminder.getId() != null && !requestIds.contains(reminder.getId()))
                .toList();
        todoReminderRepository.deleteAll(deletedReminders);
        todoReminderRepository.saveAll(result);
    }
}
