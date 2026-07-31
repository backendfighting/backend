package com.fighting.goaltracker.domain.goal.entity;

import com.fighting.goaltracker.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
@Getter
@Setter
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String category;

    @Column(name = "`desc`") // SQL 예약어 충돌 방지
    private String desc;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer progress = 0;
    private String status = "진행중";
    private String reason;
}