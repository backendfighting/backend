package com.fighting.goaltracker.domain.routine.repository;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Integer> {

    // 특정 유저의 모든 루틴 조회
    List<Routine> findByUserId(Integer userId);

    // 특정 유저의 루틴 중 '활성화(true)' 상태이면서, '오늘 요일(예: MON)'이 repeatDays에 포함된 루틴만 조회
    @Query("SELECT r FROM Routine r WHERE r.userId = :userId AND r.isActive = true AND r.repeatDays LIKE %:day%")
    List<Routine> findActiveRoutinesByDay(@Param("userId") Integer userId, @Param("day") String day);
}