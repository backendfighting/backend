package com.fighting.goaltracker.domain.record.repository;

import com.fighting.goaltracker.domain.record.entity.RoutineRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRecordRepository extends JpaRepository<RoutineRecord, Integer> {

    // 특정 유저의 특정 날짜 루틴 기록 전체 조회
    List<RoutineRecord> findByUser_UserIdAndRecordDate(Integer userId, LocalDate recordDate);

    // 특정 루틴이 특정 날짜에 이미 체크되었는지 확인하기 위한 용도
    Optional<RoutineRecord> findByRoutine_RoutineIdAndRecordDate(Integer routineId, LocalDate recordDate);
}