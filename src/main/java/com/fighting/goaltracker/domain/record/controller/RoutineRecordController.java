package com.fighting.goaltracker.domain.record.controller;

import com.fighting.goaltracker.domain.record.entity.RoutineRecord;
import com.fighting.goaltracker.domain.record.service.RoutineRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoutineRecordController {

    @Autowired
    private RoutineRecordService routineRecordService;

    // 1. 루틴 완료 체크/해제 토글
    // (POST/api/records/toggle?userId=1&routineId=3&date=2026-05-27)
    @PostMapping("/toggle")
    public String toggleRoutineCheck(
            @RequestParam("userId") Integer userId,
            @RequestParam("routineId") Integer routineId,
            @RequestParam("date") String dateStr) {
        return routineRecordService.toggleRoutineCheck(userId, routineId, dateStr);
    }

    // 2. 특정 날짜의 루틴 달성 기록 조회 (GET/api/records?userId=1&date=2026-05-27)
    @GetMapping
    public List<RoutineRecord> getRecordsByDate(
            @RequestParam("userId") Integer userId,
            @RequestParam("date") String dateStr) {
        return routineRecordService.getRecordsByDate(userId, dateStr);
    }
}
