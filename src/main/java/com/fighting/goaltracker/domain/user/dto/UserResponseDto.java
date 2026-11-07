package com.fighting.goaltracker.domain.user.dto;

import com.fighting.goaltracker.domain.user.entity.User;
import lombok.Getter;
import java.util.List;

@Getter
public class UserResponseDto {
    private Integer userId;
    private String name;
    private String email;
    private List<String> missionAlarmTimes; // 오늘의 미션 알림 시간들
    private boolean deviceAlarmOn; // 기기 알림 (전체 스위치)
    private boolean missionAlarmOn; // 오늘의 미션 알림
    private boolean todoAlarmOn; // 할 일 알림
    private boolean routineAlarmOn; // 루틴 알림
    private boolean goalAlarmOn; // 목표 리마인더 알림
    private String goalAlarmTime; // 목표 리마인더 시간

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.missionAlarmTimes = user.alarmTimeList();
        this.deviceAlarmOn = user.deviceAlarmEnabled();
        this.missionAlarmOn = user.missionAlarmEnabled();
        this.todoAlarmOn = user.todoAlarmEnabled();
        this.routineAlarmOn = user.routineAlarmEnabled();
        this.goalAlarmOn = user.goalAlarmEnabled();
        this.goalAlarmTime = user.goalAlarmTimeOrDefault();
    }
}