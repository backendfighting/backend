package com.fighting.goaltracker.domain.routine.dto;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import lombok.Getter;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RoutineResponseDto {
    private Integer routineId;
    private Integer userId;
    private String title;
    private String description;
    private String category;
    private List<DayOfWeek> repeatDays;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoutineResponseDto(Routine routine) {
        this.routineId = routine.getRoutineId();
        this.userId = routine.getUser().getUserId();
        this.title = routine.getTitle();
        this.description = routine.getDescription();
        this.category = routine.getCategory();
        this.repeatDays = routine.getRepeatDays();
        this.active = routine.isActive();
        this.createdAt = routine.getCreatedAt();
        this.updatedAt = routine.getUpdatedAt();
    }
}