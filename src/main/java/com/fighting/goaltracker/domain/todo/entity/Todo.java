package com.fighting.goaltracker.domain.todo.entity;

import com.fighting.goaltracker.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Integer todoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String description;

    @Column(name = "todo_date", nullable = false)
    private LocalDate todoDate;

    @Column(name = "todo_time")
    private LocalTime todoTime;

    @Column(nullable = false, length = 20)
    private String priority = "MEDIUM";

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void toggleComplete() {
        this.isCompleted = !this.isCompleted;
    }

    public void update(String title, String description, LocalDate todoDate,
            LocalTime todoTime, String priority) {
        if (title != null)
            this.title = title;
        if (description != null)
            this.description = description;
        if (todoDate != null)
            this.todoDate = todoDate;
        if (todoTime != null)
            this.todoTime = todoTime;
        if (priority != null)
            this.priority = priority;
    }
}