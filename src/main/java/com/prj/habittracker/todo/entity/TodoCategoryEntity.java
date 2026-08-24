package com.prj.habittracker.todo.entity;

import com.prj.habittracker.category.entity.CategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "todo_categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_todo_category",
                        columnNames = {"todo_id", "category_id"}
                )
        })
@Getter
@Setter
public class TodoCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private TodoEntity todo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;
}
