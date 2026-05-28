package com.fighting.goaltracker.domain.routine.entity;

import com.fighting.goaltracker.domain.user.entity.User; // 유저 엔티티 임포트
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

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
    private String isActive = "true";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}