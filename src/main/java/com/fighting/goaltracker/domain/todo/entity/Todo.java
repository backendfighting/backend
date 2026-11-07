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

    // 몇 분 전에 알림을 받을지 (비어 있으면 알림 없음, 0이면 시작 시간에)
    @Column(name = "notify_before")
    private Integer notifyBefore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 알림 설정 변경 (null이면 알림 끄기)
    public void changeNotifyBefore(Integer notifyBefore) {
        this.notifyBefore = notifyBefore;
    }

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