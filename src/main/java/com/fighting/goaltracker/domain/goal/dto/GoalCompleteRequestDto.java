package com.fighting.goaltracker.domain.goal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalCompleteRequestDto {
    private String status;
    private String reason;
}
