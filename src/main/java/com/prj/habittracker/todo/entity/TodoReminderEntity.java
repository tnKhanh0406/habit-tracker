package com.prj.habittracker.todo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "todo_reminders")
@Getter
@Setter
public class TodoReminderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private TodoEntity todo;

    @Column(nullable = false)
    private OffsetDateTime reminderTime;

    @Column(nullable = false)
    private Boolean enabled = true;

    @CreationTimestamp
    private OffsetDateTime createdAt;
}
