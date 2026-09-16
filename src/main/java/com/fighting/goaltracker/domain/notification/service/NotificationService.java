package com.fighting.goaltracker.domain.notification.service;

import com.fighting.goaltracker.domain.notification.dto.NotificationResponseDto;
import com.fighting.goaltracker.domain.notification.entity.Notification;
import com.fighting.goaltracker.domain.notification.repository.NotificationRepository;
import com.fighting.goaltracker.domain.routine.repository.RoutineRepository;
import com.fighting.goaltracker.domain.todo.entity.Todo;
import com.fighting.goaltracker.domain.todo.repository.TodoRepository;
import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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

    // 오늘의 할일
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendDailyTodoNotification() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            int todoCount = todoRepository.findByUser_UserIdAndTodoDate(user.getUserId(), today).size();
            int routineCount = routineRepository.findActiveRoutinesByDay(user.getUserId(), dayOfWeek).size();
            int totalCount = todoCount + routineCount;

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setType("DAILY_SUMMARY");
            notification.setMessage("오늘 할 일이 " + totalCount + "개 있습니다.");
            notificationRepository.save(notification);
        }
    }

    // 마감 임박
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void sendDeadlineNotification() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime rangeStart = now.plusMinutes(55);
        LocalTime rangeEnd = now.plusMinutes(65);

        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            List<Todo> todos = todoRepository.findByUser_UserIdAndTodoDateAndCompletedFalse(user.getUserId(), today);

            for (Todo todo : todos) {
                if (todo.getTodoTime() != null
                        && !todo.getTodoTime().isBefore(rangeStart)
                        && !todo.getTodoTime().isAfter(rangeEnd)) {

                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setType("DEADLINE");
                    notification.setMessage("\"" + todo.getTitle() + "\" 마감이 1시간 남았습니다.");
                    notificationRepository.save(notification);
                }
            }
        }
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
}