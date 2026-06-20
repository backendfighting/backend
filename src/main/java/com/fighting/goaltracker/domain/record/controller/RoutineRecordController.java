package com.fighting.goaltracker.domain.record.controller;

import com.fighting.goaltracker.domain.record.dto.RoutineRecordResponseDto;
import com.fighting.goaltracker.domain.record.service.RoutineRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Tag(name = "루틴 기록 (Record)", description = "날짜별 루틴 달성 여부 체크 및 기록 조회 기능")
@RestController
@RequestMapping("/api/records")
public class RoutineRecordController {

    @Autowired
    private RoutineRecordService routineRecordService;

    // 루틴 완료 체크/해제 토글
    @Operation(summary = "루틴 완료 체크/해제 토글", description = "유저 ID, 루틴 ID, 날짜를 기반으로 해당 루틴의 수행 완료 여부 반전 처리")
    @PostMapping("/toggle")
    public void toggleRoutineCheck(
            @RequestParam("routineId") Integer routineId,
            @RequestParam("date") String dateStr,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        routineRecordService.toggleRoutineCheck(userId, routineId, dateStr);
    }

    // 특정 날짜의 루틴 달성 기록 조회
    @Operation(summary = "특정 날짜의 루틴 달성 기록 조회", description = "날짜를 기준으로 해당 일자의 루틴 달성 기록 목록 조회")
    @GetMapping
    public List<RoutineRecordResponseDto> getRecordsByDate(
            @RequestParam("date") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return routineRecordService.getRecordsByDate(userId, date)
                .stream()
                .map(RoutineRecordResponseDto::new)
                .collect(java.util.stream.Collectors.toList());
    }
}
