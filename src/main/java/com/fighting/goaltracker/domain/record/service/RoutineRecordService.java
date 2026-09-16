package com.fighting.goaltracker.domain.record.service;

import com.fighting.goaltracker.domain.record.entity.RoutineRecord;
import com.fighting.goaltracker.domain.record.repository.RoutineRecordRepository;
import com.fighting.goaltracker.domain.routine.entity.Routine;
import com.fighting.goaltracker.domain.routine.repository.RoutineRepository;
import com.fighting.goaltracker.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoutineRecordService {

    @Autowired
    private RoutineRecordRepository routineRecordRepository;

    @Autowired
    private RoutineRepository routineRepository;

    // 루틴 완료 체크 토글 (프론트엔드에서 체크박스 누를 때 실행)
    @Transactional
    public void toggleRoutineCheck(Integer userId, Integer routineId, String dateStr) {
        LocalDate recordDate = LocalDate.parse(dateStr);

        // 이 루틴이 요청자 소유인지 먼저 확인
        Routine routine = routineRepository.findByRoutineIdAndUser_UserId(routineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴을 찾을 수 없습니다."));

        Optional<RoutineRecord> existingRecord = routineRecordRepository
                .findByRoutine_RoutineIdAndRecordDate(routineId, recordDate);

        if (existingRecord.isPresent()) {
            routineRecordRepository.delete(existingRecord.get());
        } else {
            User user = routine.getUser();

            RoutineRecord record = new RoutineRecord();
            record.setRoutine(routine);
            record.setUser(user);
            record.setRecordDate(recordDate);
            record.setCompleted(true);
            record.setCompletedAt(LocalDateTime.now());

            routineRecordRepository.save(record);
        }
    }

    // 특정 날짜의 루틴 달성 기록 목록 조회 (달력이나 일별 화면용)
    @Transactional(readOnly = true)
    public List<RoutineRecord> getRecordsByDate(Integer userId, LocalDate date) {
        return routineRecordRepository.findByUser_UserIdAndRecordDate(userId, date);
    }
}