package com.fighting.goaltracker.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // '오늘의 미션' 알림 시간들 ("08:00,21:30" 형태). 한 번도 설정 안 했으면 비어 있음 -> 아침 8시
    @JsonIgnore
    @Column(name = "mission_alarm_times", length = 255)
    private String missionAlarmTimes;

    // 기기 알림 (전체 알림 스위치). 꺼져 있으면 어떤 알림도 만들지 않는다. 비어 있으면 꺼짐
    @JsonIgnore
    @Column(name = "device_alarm_on")
    private Boolean deviceAlarmOn;

    // 알림 종류별 켜짐 여부 (비어 있으면 꺼짐 = 기본값)
    // 할 일·루틴 알림 스위치는 더 이상 쓰지 않는다 (할 일·루틴마다 정한 알림대로 보냄)
    @JsonIgnore
    @Column(name = "mission_alarm_on")
    private Boolean missionAlarmOn;

    @JsonIgnore
    @Column(name = "todo_alarm_on")
    private Boolean todoAlarmOn;

    @JsonIgnore
    @Column(name = "routine_alarm_on")
    private Boolean routineAlarmOn;

    @JsonIgnore
    @Column(name = "goal_alarm_on")
    private Boolean goalAlarmOn;

    // 목표 리마인더 알림 시간 ("09:00" 형태, 비어 있으면 오전 9시)
    @JsonIgnore
    @Column(name = "goal_alarm_time", length = 5)
    private String goalAlarmTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 오늘의 미션 알림 시간 목록 (설정한 적 없으면 ["08:00"], 전부 지웠으면 빈 목록)
    public List<String> alarmTimeList() {
        if (missionAlarmTimes == null)
            return new ArrayList<>(List.of("08:00"));
        if (missionAlarmTimes.isBlank())
            return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(missionAlarmTimes.split(",")));
    }

    public boolean deviceAlarmEnabled() {
        return Boolean.TRUE.equals(deviceAlarmOn);
    }

    public boolean missionAlarmEnabled() {
        return Boolean.TRUE.equals(missionAlarmOn);
    }

    public boolean todoAlarmEnabled() {
        return Boolean.TRUE.equals(todoAlarmOn);
    }

    public boolean routineAlarmEnabled() {
        return Boolean.TRUE.equals(routineAlarmOn);
    }

    public boolean goalAlarmEnabled() {
        return Boolean.TRUE.equals(goalAlarmOn);
    }

    // 목표 리마인더 시간 (설정한 적 없으면 오전 9시)
    public String goalAlarmTimeOrDefault() {
        return (goalAlarmTime == null || goalAlarmTime.isBlank()) ? "09:00" : goalAlarmTime;
    }
}
