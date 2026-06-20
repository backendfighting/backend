package com.fighting.goaltracker.domain.routine.controller;

import com.fighting.goaltracker.domain.routine.dto.RoutineRequestDto;
import com.fighting.goaltracker.domain.routine.dto.RoutineResponseDto;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import com.fighting.goaltracker.domain.routine.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@Tag(name = "루틴(Routine)", description = "루틴 생성, 조회, 수정 및 삭제 관련 API")
@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    // 루틴 생성
    @Operation(summary = "루틴 생성", description = "유저 ID 및 루틴 정보를 기반으로 새로운 루틴을 등록")
    @PostMapping
    public RoutineResponseDto createRoutine(@RequestBody RoutineRequestDto request, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        Routine routine = new Routine();
        routine.setTitle(request.getTitle());
        routine.setDescription(request.getDescription());
        routine.setCategory(request.getCategory());
        routine.setRepeatDays(request.getRepeatDays());
        return new RoutineResponseDto(routineService.createRoutine(userId, routine));
    }

    // 날짜별 루틴 목록 조회
    @Operation(summary = "날짜별 루틴 목록 조회", description = "특정 날짜의 루틴 목록 조회")
    @GetMapping("/list")
    public List<RoutineResponseDto> getRoutinesByDate(
            @RequestParam("date") LocalDate date,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return routineService.getRoutinesByDate(userId, date)
                .stream()
                .map(RoutineResponseDto::new)
                .collect(java.util.stream.Collectors.toList());
    }

    // 루틴 상세 조회
    @Operation(summary = "루틴 상세 조회", description = "루틴 고유 ID를 이용하여 특정 루틴의 상세 정보를 조회")
    @GetMapping("/{routineId}")
    public RoutineResponseDto getRoutineById(@PathVariable("routineId") Integer routineId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return new RoutineResponseDto(routineService.getRoutineById(routineId));
    }

    // 루틴 수정
    @Operation(summary = "루틴 수정", description = "루틴 고유 ID 및 수정할 데이터를 입력받아 기존 루틴 정보를 변경")
    @PutMapping("/{routineId}")
    public RoutineResponseDto updateRoutine(@PathVariable("routineId") Integer routineId,
            @RequestBody RoutineRequestDto request, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        Routine routineDetails = new Routine();
        routineDetails.setTitle(request.getTitle());
        routineDetails.setDescription(request.getDescription());
        routineDetails.setCategory(request.getCategory());
        routineDetails.setRepeatDays(request.getRepeatDays());
        return new RoutineResponseDto(routineService.updateRoutine(routineId, routineDetails));
    }

    // 루틴 활성화/비활성화 토글
    @Operation(summary = "루틴 활성화/비활성화 토글", description = "루틴 고유 ID를 이용하여 루틴의 활성 상태를 반전(ON/OFF)시킴")
    @PatchMapping("/{routineId}/toggle")
    public RoutineResponseDto toggleRoutineActive(@PathVariable("routineId") Integer routineId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return new RoutineResponseDto(routineService.toggleRoutineActive(routineId));
    }
}