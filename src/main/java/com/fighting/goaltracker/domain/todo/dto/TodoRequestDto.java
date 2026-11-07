package com.fighting.goaltracker.domain.todo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TodoRequestDto {
    private String title;
    private String description;
    private LocalDate todoDate;
    private LocalTime todoTime;
    private String priority;
    private Integer notifyBefore; // 몇 분 전 알림 (비우면 알림 없음)
}