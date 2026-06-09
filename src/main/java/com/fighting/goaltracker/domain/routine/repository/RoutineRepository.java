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
    List<Routine> findByUser_UserId(Integer userId);

    // routine_repeat_days에서 오늘 요일(DayOfWeek)과 일치하고 활성화된 루틴 조회 (오늘의 루틴)
    @Query("SELECT r FROM Routine r JOIN r.repeatDays d WHERE r.user.userId = :userId AND r.isActive = true AND d = :dayOfWeek")
    List<Routine> findActiveRoutinesByDay(@Param("userId") Integer userId, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // 같은 유저가 같은 이름의 루틴을 가지고 있는지 확인
    List<Routine> findByUser_UserIdAndTitle(Integer userId, String title);

    void deleteByUser_UserId(Integer userId);
}