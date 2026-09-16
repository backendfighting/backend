package com.fighting.goaltracker.domain.goal.dto;

import com.fighting.goaltracker.domain.goal.entity.Goal;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Getter
public class GoalResponseDto {
    private Integer goalId;
    private String title;
    private String category;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer progress;
    private String status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GoalResponseDto(Goal goal) {
        this.goalId = goal.getGoalId();
        this.title = goal.getTitle();
        this.category = goal.getCategory();
        this.description = goal.getDescription();
        this.startDate = goal.getStartDate();
        this.endDate = goal.getEndDate();
        this.progress = goal.getProgress();
        this.status = goal.getStatus();
        this.reason = goal.getReason();
        this.createdAt = goal.getCreatedAt();
        this.updatedAt = goal.getUpdatedAt();
    }
}
