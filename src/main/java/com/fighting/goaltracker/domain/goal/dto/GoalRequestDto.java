package com.fighting.goaltracker.domain.goal.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class GoalRequestDto {
    private String title;
    private String category;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer progress;
    private String status;
    private String reason;
}