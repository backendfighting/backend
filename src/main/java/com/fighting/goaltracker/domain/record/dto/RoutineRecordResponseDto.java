package com.fighting.goaltracker.domain.record.dto;

import com.fighting.goaltracker.domain.record.entity.RoutineRecord;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RoutineRecordResponseDto {

    private final Integer routineRecordId;

    // 루틴 정보
    private final Integer routineId;
    private final String routineTitle;
    private final String category;
    private final List<DayOfWeek> repeatDays;

    // 기록 정보
    private final LocalDate recordDate;
    private final boolean completed;
    private final LocalDateTime completedAt;

    // 생성자 (Entity → DTO 변환)
    public RoutineRecordResponseDto(RoutineRecord record) {
        this.routineRecordId = record.getRoutineRecordId();

        this.routineId = record.getRoutine().getRoutineId();
        this.routineTitle = record.getRoutine().getTitle();
        this.category = record.getRoutine().getCategory();
        this.repeatDays = record.getRoutine().getRepeatDays();

        this.recordDate = record.getRecordDate();
        this.completed = record.isCompleted();
        this.completedAt = record.getCompletedAt();
    }
}