package com.fighting.goaltracker.domain.user.service;

import com.fighting.goaltracker.domain.user.dto.AlarmSettingsRequestDto;
import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.repository.UserRepository;
import com.fighting.goaltracker.domain.user.dto.UpdateProfileRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.TreeSet;

import com.fighting.goaltracker.domain.todo.repository.TodoRepository;
import com.fighting.goaltracker.domain.routine.repository.RoutineRepository;
import com.fighting.goaltracker.domain.record.repository.RoutineRecordRepository;
import com.fighting.goaltracker.domain.goal.repository.GoalRepository;
import com.fighting.goaltracker.domain.notification.repository.NotificationRepository;

@Service
public class UserService {

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

    @Autowired
    private NotificationRepository notificationRepository;

    // 비밀번호 암호화 도구
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입
    @Transactional
    public User signup(User user) {
        // 중복 이메일 확인
        userRepository.findByEmail(user.getEmail())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("이미 가입된 이메일 주소입니다.");
                });

        // 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 사용자 조회 (ID로 찾기)
    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 로그인
    @Transactional(readOnly = true)
    public User login(String email, String password) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 입력한 비밀번호를 암호화된 비밀번호와 비교
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // 내 정보 수정
    @Transactional
    public User updateProfile(Integer currentUserId, UpdateProfileRequestDto updateRequest) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null) {
            // 이미 사용 중인 이메일로 변경 시도 차단
            userRepository.findByEmail(updateRequest.getEmail())
                    .filter(existing -> !existing.getUserId().equals(currentUserId))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("이미 사용 중인 이메일 주소입니다.");
                    });
            user.setEmail(updateRequest.getEmail());
        }

        return userRepository.save(user);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(Integer currentUserId, String currentPassword, String newPassword) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인 (암호화된 비밀번호와 비교)
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 오늘의 미션 알림 시간 변경 ("HH:mm" 목록, 빈 목록이면 알림 끄기)
    @Transactional
    public User updateMissionAlarmTimes(Integer userId, List<String> times) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setMissionAlarmTimes(cleanAlarmTimes(times));
        return userRepository.save(user);
    }

    // 알림 설정 변경 (보낸 항목만 반영)
    @Transactional
    public User updateAlarmSettings(Integer userId, AlarmSettingsRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (request.getDeviceAlarmOn() != null) {
            user.setDeviceAlarmOn(request.getDeviceAlarmOn());
            // 처음 켤 때(오늘의 미션·목표 리마인더를 한 번도 정한 적 없을 때)는 둘 다 켠다.
            // 그 뒤로는 껐다 켜도 이전에 정해둔 설정 그대로 돌아온다.
            if (request.getDeviceAlarmOn() && user.getMissionAlarmOn() == null && user.getGoalAlarmOn() == null) {
                user.setMissionAlarmOn(true);
                user.setGoalAlarmOn(true);
            }
        }
        if (request.getMissionAlarmOn() != null)
            user.setMissionAlarmOn(request.getMissionAlarmOn());
        if (request.getMissionAlarmTimes() != null)
            user.setMissionAlarmTimes(cleanAlarmTimes(request.getMissionAlarmTimes()));
        if (request.getTodoAlarmOn() != null)
            user.setTodoAlarmOn(request.getTodoAlarmOn());
        if (request.getRoutineAlarmOn() != null)
            user.setRoutineAlarmOn(request.getRoutineAlarmOn());
        if (request.getGoalAlarmOn() != null)
            user.setGoalAlarmOn(request.getGoalAlarmOn());
        if (request.getGoalAlarmTime() != null) {
            if (!isHourMinute(request.getGoalAlarmTime()))
                throw new IllegalArgumentException("목표 리마인더 시간 형식이 올바르지 않습니다: " + request.getGoalAlarmTime());
            user.setGoalAlarmTime(request.getGoalAlarmTime());
        }
        return userRepository.save(user);
    }

    private boolean isHourMinute(String t) {
        return t != null && t.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    // "HH:mm" 목록 검사 + 중복 제거 + 시간순 정렬 -> "08:00,21:30"
    private String cleanAlarmTimes(List<String> times) {
        TreeSet<String> cleaned = new TreeSet<>();
        if (times != null) {
            for (String t : times) {
                if (!isHourMinute(t)) {
                    throw new IllegalArgumentException("알림 시간 형식이 올바르지 않습니다: " + t);
                }
                cleaned.add(t);
            }
        }
        if (cleaned.size() > 10) {
            throw new IllegalArgumentException("알림 시간은 10개까지 설정할 수 있습니다.");
        }
        return String.join(",", cleaned);
    }

    // 회원 탈퇴 (회원 정보 삭제)
    @Transactional
    public void deleteUser(Integer userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 본인 확인: 비밀번호가 맞아야 탈퇴할 수 있음
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        notificationRepository.deleteByUser_UserId(userId);
        goalRepository.deleteByUser_UserId(userId);
        routineRecordRepository.deleteByUser_UserId(userId);
        routineRepository.deleteByUser_UserId(userId);
        todoRepository.deleteByUser_UserId(userId);
        userRepository.delete(user);
    }
}