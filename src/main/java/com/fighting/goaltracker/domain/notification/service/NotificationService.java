package com.fighting.goaltracker.domain.notification.service;

import com.fighting.goaltracker.domain.goal.entity.Goal;
import com.fighting.goaltracker.domain.goal.repository.GoalRepository;
import com.fighting.goaltracker.domain.notification.dto.NotificationResponseDto;
import com.fighting.goaltracker.domain.notification.entity.Notification;
import com.fighting.goaltracker.domain.notification.repository.NotificationRepository;
import com.fighting.goaltracker.domain.record.repository.RoutineRecordRepository;
import com.fighting.goaltracker.domain.routine.entity.Routine;
import com.fighting.goaltracker.domain.routine.repository.RoutineRepository;
import com.fighting.goaltracker.domain.todo.entity.Todo;
import com.fighting.goaltracker.domain.todo.repository.TodoRepository;
import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineRecordRepository routineRecordRepository;

    @Autowired
    private GoalRepository goalRepository;

    // 서버 컴퓨터의 시간대와 상관없이 한국 시간 기준으로 동작하도록 고정
    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    // 매 분 0초마다 확인해서, 알림 시각이 된 것만 알림을 만든다
    // (알림 설정에서 해당 종류를 켠 사용자만 받음. 기본값은 모두 꺼짐)
    // 1) 할 일: 알림을 켠 미완료 할 일 -> 제목 "오늘의 할일", 내용 "산책하기 17:00"
    // 2) 루틴: 알림을 켠, 그날 아직 안 한 루틴 -> 제목 "매일 루틴", 내용 "물 마시기 08:00"
    // 3) 오늘의 미션: 사용자가 정한 시간마다, 남은 미션이 있을 때만 -> "오늘의 남은 미션은 n개 입니다."
    // 4) 목표 리마인더: 리마인더를 켠 진행중 목표의 마감일, 정한 시간에 -> "'토익' 목표의 마감일이 오늘이에요."
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now(KOREA).withSecond(0).withNano(0);
        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);
        String nowHm = now.toLocalTime().format(HM);

        for (User user : userRepository.findAll()) {
            Integer userId = user.getUserId();

            // 기기 알림(전체 스위치)이 꺼져 있으면 아무 알림도 만들지 않는다
            if (!user.deviceAlarmEnabled())
                continue;

            // 1) 할 일 (자정 직후 일정은 전날 밤에 울릴 수 있으므로 내일 일정도 확인)
            for (LocalDate day : List.of(today, tomorrow)) {
                for (Todo todo : todoRepository.findByUser_UserIdAndTodoDateAndCompletedFalse(userId, day)) {
                    if (todo.getTodoTime() == null || todo.getNotifyBefore() == null)
                        continue;
                    LocalDateTime alarmAt = day.atTime(todo.getTodoTime()).minusMinutes(todo.getNotifyBefore());
                    if (alarmAt.equals(now)) {
                        saveNotification(user, "TODO", todo.getTitle() + " " + todo.getTodoTime().format(HM));
                    }
                }
            }

            // 2) 루틴
            for (LocalDate day : List.of(today, tomorrow)) {
                for (Routine routine : routineRepository.findActiveRoutinesByDay(userId, day.getDayOfWeek())) {
                    if (!routine.appearsOn(day))
                        continue;
                    if (routine.getRoutineTime() == null || routine.getNotifyBefore() == null)
                        continue;
                    LocalDateTime alarmAt = day.atTime(routine.getRoutineTime())
                            .minusMinutes(routine.getNotifyBefore());
                    if (!alarmAt.equals(now))
                        continue;
                    boolean alreadyDone = routineRecordRepository
                            .findByRoutine_RoutineIdAndRecordDate(routine.getRoutineId(), day)
                            .isPresent();
                    if (!alreadyDone) {
                        saveNotification(user, "ROUTINE",
                                routine.getTitle() + " " + routine.getRoutineTime().format(HM));
                    }
                }
            }

            // 3) 오늘의 미션
            if (user.missionAlarmEnabled() && user.alarmTimeList().contains(nowHm)) {
                int remain = todoRepository.findByUser_UserIdAndTodoDateAndCompletedFalse(userId, today).size();
                for (Routine routine : routineRepository.findActiveRoutinesByDay(userId, today.getDayOfWeek())) {
                    if (!routine.appearsOn(today))
                        continue;
                    boolean done = routineRecordRepository
                            .findByRoutine_RoutineIdAndRecordDate(routine.getRoutineId(), today)
                            .isPresent();
                    if (!done)
                        remain++;
                }
                // 남은 미션이 없으면 울리지 않는다
                if (remain > 0) {
                    saveNotification(user, "MISSION", "오늘의 남은 미션은 " + remain + "개 입니다.");
                }
            }

            // 4) 목표 리마인더 (마감일 당일 + 마감 다음날)
            if (user.goalAlarmEnabled() && nowHm.equals(user.goalAlarmTimeOrDefault())) {
                for (Goal goal : goalRepository.findByUser_UserId(userId)) {
                    boolean remind = Boolean.TRUE.equals(goal.getRemindOn());
                    boolean inProgress = "진행중".equals(goal.getStatus());
                    if (remind && inProgress && today.equals(goal.getEndDate())) {
                        saveNotification(user, "GOAL", "'" + goal.getTitle() + "' 목표의 마감일이 오늘이에요.");
                    }
                    // 마감 다음날 한 번: 아직 결과(성공/실패)를 안 적었으면 상태 업데이트를 부탁한다
                    if (remind && inProgress && goal.getEndDate() != null
                            && today.equals(goal.getEndDate().plusDays(1))) {
                        saveNotification(user, "GOAL_OVERDUE", "'" + goal.getTitle()
                                + "'의 마감일이 지났어요. 목표의 성공 여부를 확인하고 상태를 업데이트해 주세요.");
                    }
                }
            }
        }
    }

    private void saveNotification(User user, String type, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    // 알림 목록 조회
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotifications(Integer userId) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponseDto::new)
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository.findByNotificationIdAndUser_UserId(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림을 찾을 수 없습니다."));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 알림 삭제 (내 알림만 지울 수 있음)
    @Transactional
    public void deleteNotification(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository.findByNotificationIdAndUser_UserId(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림을 찾을 수 없습니다."));
        notificationRepository.delete(notification);
    }
}