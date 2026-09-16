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

        routine.setUser(user);
        routine.setActive(true);
        return routineRepository.save(routine);
    }

    // 특정 유저의 오늘 해야 하는 활성화된 루틴 목록 조회
    @Transactional(readOnly = true)
    public List<Routine> getRoutinesByDate(Integer userId, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return routineRepository.findActiveRoutinesByDay(userId, dayOfWeek);
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

        return routineRepository.save(existingRoutine);
    }

    // 루틴 켜고 끄기
    @Transactional
    public Routine toggleRoutineActive(Integer routineId, Integer userId) {
        Routine routine = routineRepository.findByRoutineIdAndUser_UserId(routineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다."));

        routine.setActive(!routine.isActive());
        return routineRepository.save(routine);
    }

    // 루틴 삭제
    @Transactional
    public void deleteRoutine(Integer routineId, Integer userId) {
        Routine routine = routineRepository.findByRoutineIdAndUser_UserId(routineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다."));

        routineRecordRepository.deleteByRoutine_RoutineId(routineId);
        routineRepository.delete(routine);
    }
}