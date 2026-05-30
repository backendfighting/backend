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

    // 1. 루틴 완료 체크 토글 (프론트엔드에서 체크박스 딸깍 누를 때 실행)
    @Transactional
    public String toggleRoutineCheck(Integer userId, Integer routineId, String dateStr) {
        LocalDate recordDate = LocalDate.parse(dateStr);

        // 해당 날짜에 이미 기록이 있는지 확인
        Optional<RoutineRecord> existingRecord = routineRecordRepository
                .findByRoutine_RoutineIdAndRecordDate(routineId, recordDate);

        if (existingRecord.isPresent()) {
            // 이미 기록이 존재한다면 -> 체크 해제 요청이므로 기록을 삭제
            routineRecordRepository.delete(existingRecord.get());
            return "체크 해제 완료 (기록 삭제)";
        } else {
            // 기록이 없다면 -> 새롭게 완료 처리
            Routine routine = routineRepository.findById(routineId)
                    .orElseThrow(() -> new RuntimeException("해당 루틴을 찾을 수 없습니다."));

            User user = routine.getUser(); // 루틴에 묶여있는 유저 객체 가져오기

            RoutineRecord record = new RoutineRecord();
            record.setRoutine(routine);
            record.setUser(user);
            record.setRecordDate(recordDate);
            record.setIsCompleted("true");
            record.setCompletedAt(LocalDateTime.now()); // 현재 완수 시각 저장

            routineRecordRepository.save(record);
            return "체크 완료 (기록 생성)";
        }
    }

    // 2. 특정 날짜의 루틴 달성 기록 목록 조회 (달력이나 일별 화면용)
    @Transactional(readOnly = true)
    public List<RoutineRecord> getRecordsByDate(Integer userId, String dateStr) {
        LocalDate recordDate = LocalDate.parse(dateStr);
        return routineRecordRepository.findByUser_UserIdAndRecordDate(userId, recordDate);
    }
}