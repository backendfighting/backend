package com.fighting.goaltracker.domain.routine.service;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import com.fighting.goaltracker.domain.routine.repository.RoutineRepository;
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

    // 루틴 생성
    @Transactional
    public Routine createRoutine(Integer userId, Routine routine) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        routine.setUser(user);
        routine.setActive(true);
        return routineRepository.save(routine);
    }

    // 특정 유저의 '오늘' 해야 하는 활성화된 루틴 목록 조회
    @Transactional(readOnly = true)
    public List<Routine> getTodayRoutines(Integer userId) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        // Repository의 커스텀 쿼리 호출 (Enum 객체를 그대로 넘김)
        return routineRepository.findActiveRoutinesByDay(userId, today);
    }

    // 루틴 상세 조회 로직
    @Transactional(readOnly = true)
    public Routine getRoutineById(Integer routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다. ID: " + routineId));
    }

    // 루틴 수정 로직
    @Transactional
    public Routine updateRoutine(Integer routineId, Routine routineDetails) {
        Routine existingRoutine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 루틴입니다."));

        existingRoutine.setTitle(routineDetails.getTitle());
        existingRoutine.setDescription(routineDetails.getDescription());
        existingRoutine.setCategory(routineDetails.getCategory());
        existingRoutine.setRepeatDays(routineDetails.getRepeatDays());

        return routineRepository.save(existingRoutine);
    }

    // 루틴 켜고 끄기 (is_active 토글 스위치)
    @Transactional
    public Routine toggleRoutineActive(Integer routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("해당 루틴을 찾을 수 없습니다."));

        // "true" <-> "false" 전환
        routine.setActive(!routine.isActive());

        return routineRepository.save(routine);
    }
}