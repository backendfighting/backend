package com.fighting.goaltracker.domain.routine.dto;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import lombok.Getter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime routineTime;
    private String priority;
    private Integer notifyBefore;
    private List<PauseDto> pauses;
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
        this.startDate = routine.getStartDate();
        this.endDate = routine.getEndDate();
        this.routineTime = routine.getRoutineTime();
        this.priority = routine.getPriority();
        this.notifyBefore = routine.getNotifyBefore();
        this.pauses = routine.getEffectivePauses().stream()
                .map(p -> new PauseDto(p.getPausedFrom(), p.getResumedOn()))
                .collect(java.util.stream.Collectors.toList());
        this.createdAt = routine.getCreatedAt();
        this.updatedAt = routine.getUpdatedAt();
    }

    // 일시정지 기간 (pausedFrom <= 날짜 < resumedOn, resumedOn이 비어 있으면 아직 쉬는 중)
    @Getter
    public static class PauseDto {
        private LocalDate pausedFrom;
        private LocalDate resumedOn;

        public PauseDto(LocalDate pausedFrom, LocalDate resumedOn) {
            this.pausedFrom = pausedFrom;
            this.resumedOn = resumedOn;
        }
    }
}
