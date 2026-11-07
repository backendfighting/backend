package com.fighting.goaltracker.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

// 알림 설정 변경 요청. 보낸 항목만 바뀐다 (보내지 않은 항목은 그대로)
@Getter
@Setter
public class AlarmSettingsRequestDto {
    private Boolean deviceAlarmOn;
    private Boolean missionAlarmOn;
    private List<String> missionAlarmTimes;
    private Boolean todoAlarmOn;
    private Boolean routineAlarmOn;
    private Boolean goalAlarmOn;
    private String goalAlarmTime;
}
