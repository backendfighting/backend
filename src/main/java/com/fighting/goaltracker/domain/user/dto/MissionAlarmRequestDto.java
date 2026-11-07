package com.fighting.goaltracker.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

// 오늘의 미션 알림 시간 변경 요청: { "times": ["08:00", "21:30"] }
@Getter
@Setter
public class MissionAlarmRequestDto {
    private List<String> times;
}
