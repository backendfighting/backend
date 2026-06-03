package com.fighting.goaltracker.domain.todo.dto;

import com.fighting.goaltracker.domain.todo.entity.Todo;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TodoResponseDto {
    private Integer todoId;
    private String title;
    private String description;
    private LocalDate todoDate;
    private LocalTime todoTime;
    private String priority;
    private boolean completed;

    // Todo 엔티티를 TodoResponseDto로 변환하는 생성자
    public TodoResponseDto(Todo todo) {
        this.todoId = todo.getTodoId();
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.todoDate = todo.getTodoDate();
        this.todoTime = todo.getTodoTime();
        this.priority = todo.getPriority();
        this.completed = todo.isCompleted();
    }
}
