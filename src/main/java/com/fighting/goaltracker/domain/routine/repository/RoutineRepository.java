package com.fighting.goaltracker.domain.routine.repository;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;

import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Integer> {

    // 특정 유저의 모든 루틴 조회
    List<Routine> findByUserId(Integer userId);

    // 💡 1:N 컬렉션 테이블(routine_repeat_days)에서 오늘 요일(DayOfWeek)과 일치하고 활성화된 루틴 조회
    @Query("SELECT r FROM Routine r JOIN r.repeatDays d WHERE r.user.id = :userId AND r.isActive = 'true' AND d = :dayOfWeek")
    List<Routine> findActiveRoutinesByDay(@Param("userId") Integer userId, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}