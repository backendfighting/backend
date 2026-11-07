package com.fighting.goaltracker.domain.routine.service;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import com.fighting.goaltracker.domain.routine.repository.RoutineRepository;
import com.fighting.goaltracker.domain.record.repository.RoutineRecordRepository;
import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoutineRecordRepository routineRecordRepository;

    // 루틴 생성
    @Transactional
    public Routine createRoutine(Integer userId, Routine routine) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // ✨ 발표 데모 및 프론트 연동 최적화: 유효 카테고리 허용 범위 확장
        if (routine.getCategory() == null || routine.getCategory().isEmpty()) {
            routine.setCategory("기타");
        }

        if (routine.getPriority() == null || routine.getPriority().isEmpty()) {
            routine.setPriority("MEDIUM");
        }

        routine.setUser(user);
        routine.setActive(true);
        routine.setEndDate(null);
        return routineRepository.save(routine);
    }

    // 특정 유저의 모든 루틴 조회 (일시중지된 루틴 포함)
    @Transactional(readOnly = true)
    public List<Routine> getAllRoutines(Integer userId) {
        return routineRepository.findByUser_UserId(userId);
    }

    // 특정 유저의 오늘 해야 하는 활성화된 루틴 목록 조회
    @Transactional(readOnly = true)
    public List<Routine> getRoutinesByDate(Integer userId, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        // 요일이 맞고, 시작일~종료일 사이이고, 그 날 쉬는 중이 아닌 루틴만 보이게 한다
        // (지금은 일시정지 중이어도, 쉬기 전 날짜에는 그대로 보인다)
        return routineRepository.findByUser_UserId(userId)
                .stream()
                .filter(routine -> routine.getRepeatDays() != null && routine.getRepeatDays().contains(dayOfWeek))
                .filter(routine -> routine.appearsOn(date))
                .filter(routine -> !routine.isPausedOn(date))
                .collect(Collectors.toList());
    }

    // 루틴 상세 조회
    @Transactional(readOnly = true)
    public Routine getRoutineById(Integer routineId, Integer userId) {
        return routineRepository.findByRoutineIdAndUser_UserId(routineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다."));
    }

    // 루틴 수정
    @Transactional
    public Routine updateRoutine(Integer routineId, Integer userId, Routine routineDetails) {
        Routine existingRoutine = routineRepository.findByRoutineIdAndUser_UserId(routineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다."));

        if (routineDetails.getTitle() != null)
            existingRoutine.setTitle(routineDetails.getTitle());
        if (routineDetails.getDescription() != null)
            existingRoutine.setDescription(routineDetails.getDescription());
        if (routineDetails.getCategory() != null)
            existingRoutine.setCategory(routineDetails.getCategory());
        if (routineDetails.getRepeatDays() != null)
            existingRoutine.setRepeatDays(routineDetails.getRepeatDays());
        if (routineDetails.getPriority() != null)
            existingRoutine.setPriority(routineDetails.getPriority());

        // 시작일·시간·알림은 '비우기'도 가능해야 하므로 보낸 값 그대로 덮어쓴다
        existingRoutine.setStartDate(routineDetails.getStartDate());
        existingRoutine.setRoutineTime(routineDetails.getRoutineTime());
        existingRoutine.setNotifyBefore(routineDetails.getNotifyBefore());

        return routineRepository.save(existingRoutine);
    }

    // 루틴 켜고 끄기
    @Transactional
    public Routine toggleRoutineActive(Integer routineId, Integer userId) {
        Routine routine = routineRepository.findByRoutineIdAndUser_UserId(routineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다."));

        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        if (routine.isActive()) {
            // 오늘 이미 했으면 오늘은 남기고 내일부터 쉰다
            boolean doneToday = routineRecordRepository
                    .findByRoutine_RoutineIdAndRecordDate(routineId, today)
                    .isPresent();
            routine.pause(doneToday ? today.plusDays(1) : today);
        } else {
            routine.resume(today);
        }
        return routineRepository.save(routine);
    }

    // 루틴 삭제
    // 지난 기록(통계)은 남기고, 기준 날짜부터 달력에서 사라지도록 종료일만 정한다.
    // - 기준 날짜에 아직 안 했으면: 그 날부터 사라짐
    // - 기준 날짜에 이미 했으면: 그 날은 남기고 다음 날부터 사라짐
    @Transactional
    public void deleteRoutine(Integer routineId, Integer userId, LocalDate baseDate) {
        Routine routine = routineRepository.findByRoutineIdAndUser_UserId(routineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다."));

        LocalDate base = (baseDate != null) ? baseDate : LocalDate.now();
        boolean doneOnBase = routineRecordRepository
                .findByRoutine_RoutineIdAndRecordDate(routineId, base)
                .isPresent();

        routine.setEndDate(doneOnBase ? base.plusDays(1) : base);
        routineRepository.save(routine);
    }
}