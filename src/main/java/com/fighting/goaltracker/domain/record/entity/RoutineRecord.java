package com.fighting.goaltracker.domain.record.entity;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import com.fighting.goaltracker.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
// 같은 루틴을 같은 날짜에 두 번 기록하지 못하게 막는다 (빠른 연속 클릭 대비)
@Table(name = "routine_records", uniqueConstraints = @UniqueConstraint(name = "uk_routine_record_date", columnNames = { "routine_id", "record_date" }))
@Getter
@Setter
public class RoutineRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_record_id")
    private Integer routineRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

}