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

    @Column(length = 255)
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer progress = 0;
    private String status = "진행중";
    private String reason;

    public void update(String title, String category, String description,
            LocalDate startDate, LocalDate endDate,
            Integer progress, String status, String reason) {
        if (title != null)
            this.title = title;
        if (category != null)
            this.category = category;
        if (description != null)
            this.description = description;
        if (startDate != null)
            this.startDate = startDate;
        if (endDate != null)
            this.endDate = endDate;
        if (progress != null)
            this.progress = progress;
        if (status != null)
            this.status = status;
        if (reason != null)
            this.reason = reason;
    }
}