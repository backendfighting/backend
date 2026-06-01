package com.fighting.goaltracker.domain.routine.controller;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import com.fighting.goaltracker.domain.routine.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "루틴(Routine)", description = "루틴 생성, 조회, 수정 및 삭제 관련 API")
@RestController
@RequestMapping("/api/routines")
@CrossOrigin(origins = "*", allowedHeaders = "*") // 프론트 협업용 CORS 임시 허용
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    // 루틴 생성 (POST /api/routines?userId=1)
    @Operation(summary = "루틴 생성", description = "유저 ID 및 루틴 정보를 기반으로 새로운 루틴을 등록")
    @PostMapping
    public Routine createRoutine(@RequestParam("userId") Integer userId, @RequestBody Routine routine) {
        return routineService.createRoutine(userId, routine);
    }

    // 오늘의 루틴 목록 조회 (GET /api/routines/today?userId=1)
    @Operation(summary = "오늘의 루틴 목록 조회", description = "유저 ID를 기준으로 금일 수행해야 할 루틴 목록 조회")
    @GetMapping("/today")
    public List<Routine> getTodayRoutines(@RequestParam("userId") Integer userId) {
        return routineService.getTodayRoutines(userId);
    }

    // 루틴 상세 조회 (GET /api/routines/1)
    @Operation(summary = "루틴 상세 조회", description = "루틴 고유 ID를 이용하여 특정 루틴의 상세 정보를 조회")
    @GetMapping("/{routineId}")
    public Routine getRoutineById(@PathVariable("routineId") Integer routineId) {
        return routineService.getRoutineById(routineId);
    }

    // 루틴 수정 (PUT /api/routines/1)
    @Operation(summary = "루틴 수정", description = "루틴 고유 ID 및 수정할 데이터를 입력받아 기존 루틴 정보를 변경")
    @PutMapping("/{routineId}")
    public Routine updateRoutine(@PathVariable("routineId") Integer routineId, @RequestBody Routine routineDetails) {
        return routineService.updateRoutine(routineId, routineDetails);
    }

    // 루틴 활성화/비활성화 토글 (PATCH /api/routines/{routineId}/toggle)
    @Operation(summary = "루틴 활성화/비활성화 토글", description = "루틴 고유 ID를 이용하여 루틴의 활성 상태를 반전(ON/OFF)시킴")
    @PatchMapping("/{routineId}/toggle")
    public Routine toggleRoutineActive(@PathVariable("routineId") Integer routineId) {
        return routineService.toggleRoutineActive(routineId);
    }
}