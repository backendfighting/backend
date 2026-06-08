package com.fighting.goaltracker.domain.record.repository;

import com.fighting.goaltracker.domain.record.entity.RoutineRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRecordRepository extends JpaRepository<RoutineRecord, Integer> {

    // 1️⃣ 유저 ID 매칭 수정 완료
    List<RoutineRecord> findByUserUserIdAndRecordDate(Integer userId, LocalDate recordDate);

    // 2️⃣ 루틴 ID 매칭 수정 완료 (findByRoutineId -> findByRoutineRoutineId)
    Optional<RoutineRecord> findByRoutineRoutineIdAndRecordDate(Integer routineId, LocalDate recordDate);
}