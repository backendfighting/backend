package com.fighting.goaltracker.domain.routine.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RoutineRequestDto {
    private String title;
    private String description;
    private String category;
    private List<DayOfWeek> repeatDays;
    private LocalDate startDate; // 시작일 (비우면 처음부터)
    private LocalTime routineTime; // 수행 시간 (선택)
    private String priority; // HIGH / MEDIUM / LOW
    private Integer notifyBefore; // 몇 분 전 알림 (비우면 알림 없음)
}