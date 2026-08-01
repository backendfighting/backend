package com.fighting.goaltracker.domain.goal.dto;

import com.fighting.goaltracker.domain.goal.entity.Goal;
import lombok.Getter;
import java.time.LocalDate;

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

    public GoalResponseDto(Goal goal) {
        this.goalId = goal.getId();
        this.title = goal.getTitle();
        this.category = goal.getCategory();
        this.description = goal.getDescription();
        this.startDate = goal.getStartDate();
        this.endDate = goal.getEndDate();
        this.progress = goal.getProgress();
        this.status = goal.getStatus();
        this.reason = goal.getReason();
    }
}
