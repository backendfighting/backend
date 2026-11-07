package com.fighting.goaltracker.domain.routine.entity;

import com.fighting.goaltracker.domain.user.entity.User; // 유저 엔티티 임포트
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "routines")
@Getter
@Setter
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_id")
    private Integer routineId;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정 (성능 최적화)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String description;

    @Column(length = 50)
    private String category;

    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "routine_repeat_days", joinColumns = @JoinColumn(name = "routine_id"))
    @Enumerated(EnumType.STRING) // DB에 "MONDAY", "WEDNESDAY" 문자열로 안전하게 저장됨!
    @Column(name = "day_of_week")
    private List<DayOfWeek> repeatDays; // 중복 요일 방지를 위해 Set이나 List를 씁니다.

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // 루틴 시작일: 이 날부터 달력에 보인다 (비어 있으면 처음부터)
    @Column(name = "start_date")
    private LocalDate startDate;

    // 루틴 종료일: 이 날부터 달력에서 안 보인다 (비어 있으면 계속). 루틴을 삭제하면 채워진다
    @Column(name = "end_date")
    private LocalDate endDate;

    // 루틴 수행 시간 (선택)
    @Column(name = "routine_time")
    private LocalTime routineTime;

    // 중요도: HIGH / MEDIUM / LOW
    @Column(length = 20)
    private String priority;

    // 몇 분 전에 알림을 받을지 (비어 있으면 알림 없음, 0이면 시작 시간에)
    @Column(name = "notify_before")
    private Integer notifyBefore;

    // 일시정지 기록: 쉬었던 기간은 통계(수행률)에서 빠진다
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "routine_pauses", joinColumns = @JoinColumn(name = "routine_id"))
    private List<RoutinePause> pauses = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 이 날짜에 달력에 보이는 기간인지 (시작일 <= 날짜 < 종료일)
    public boolean appearsOn(LocalDate date) {
        if (startDate != null && date.isBefore(startDate))
            return false;
        if (endDate != null && !date.isBefore(endDate))
            return false;
        return true;
    }

    // 아직 끝나지 않은(쉬는 중인) 일시정지 기록
    private RoutinePause findOpenPause() {
        if (pauses == null)
            return null;
        for (RoutinePause p : pauses) {
            if (p.getResumedOn() == null)
                return p;
        }
        return null;
    }

    // 일시정지 기록 (예전에 기록 없이 꺼둔 루틴은 마지막 수정일부터 쉬는 중으로 본다)
    public List<RoutinePause> getEffectivePauses() {
        List<RoutinePause> list = new ArrayList<>(pauses == null ? List.of() : pauses);
        if (!isActive && findOpenPause() == null) {
            LocalDate from = (updatedAt != null) ? updatedAt.toLocalDate() : LocalDate.now();
            list.add(new RoutinePause(from, null));
        }
        return list;
    }

    // 이 날짜에 쉬는 중이었는지
    public boolean isPausedOn(LocalDate date) {
        for (RoutinePause p : getEffectivePauses()) {
            if (p.covers(date))
                return true;
        }
        return false;
    }

    // 일시정지: from부터 쉰다
    public void pause(LocalDate from) {
        if (pauses == null)
            pauses = new ArrayList<>();
        if (findOpenPause() == null)
            pauses.add(new RoutinePause(from, null));
        isActive = false;
    }

    // 다시 시작: on부터 다시 보인다
    public void resume(LocalDate on) {
        if (pauses == null)
            pauses = new ArrayList<>();
        RoutinePause open = findOpenPause();
        if (open == null && !isActive && updatedAt != null) {
            // 기록 없이 꺼져 있던 예전 루틴: 마지막 수정일부터 쉰 것으로 남긴다
            open = new RoutinePause(updatedAt.toLocalDate(), null);
            pauses.add(open);
        }
        if (open != null) {
            if (!on.isAfter(open.getPausedFrom()))
                pauses.remove(open); // 같은 날 껐다 켰으면 쉰 기간이 없으니 지운다
            else
                open.setResumedOn(on);
        }
        isActive = true;
    }
}
