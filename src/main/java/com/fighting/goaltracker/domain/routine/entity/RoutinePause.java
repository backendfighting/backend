package com.fighting.goaltracker.domain.routine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// 루틴 일시정지 기간 한 번 (pausedFrom <= 날짜 < resumedOn 동안 쉬는 중)
// resumedOn이 비어 있으면 아직 쉬는 중
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class RoutinePause {

    @Column(name = "paused_from", nullable = false)
    private LocalDate pausedFrom;

    @Column(name = "resumed_on")
    private LocalDate resumedOn;

    public RoutinePause(LocalDate pausedFrom, LocalDate resumedOn) {
        this.pausedFrom = pausedFrom;
        this.resumedOn = resumedOn;
    }

    // 이 날짜가 쉬는 기간 안인지
    public boolean covers(LocalDate date) {
        if (date.isBefore(pausedFrom))
            return false;
        return resumedOn == null || date.isBefore(resumedOn);
    }
}
