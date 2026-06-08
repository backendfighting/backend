package com.fighting.goaltracker.domain.routine.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
public class RoutineRequestDto {
    private String title;
    private String description;
    private String category;
    private List<DayOfWeek> repeatDays;
}