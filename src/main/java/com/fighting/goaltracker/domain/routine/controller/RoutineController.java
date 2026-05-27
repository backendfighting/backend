package com.fighting.goaltracker.domain.routine.controller;

import com.fighting.goaltracker.domain.routine.entity.Routine;
import com.fighting.goaltracker.domain.routine.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
@CrossOrigin(origins = "*", allowedHeaders = "*") // 프론트 협업용 CORS 임시 허용
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    // 루틴 생성 (POST /api/routines?userId=1)
    @PostMapping
    public Routine createRoutine(@RequestParam("userId") Integer userId, @RequestBody Routine routine) {
        return routineService.createRoutine(userId, routine);
    }

    // 오늘의 루틴 목록 조회 (GET /api/routines/today?userId=1)
    @GetMapping("/today")
    public List<Routine> getTodayRoutines(@RequestParam("userId") Integer userId) {
        return routineService.getTodayRoutines(userId);
    }

    // 루틴 상세 조회 (GET /api/routines/1)
    @GetMapping("/{routineId}")
    public Routine getRoutineById(@PathVariable("routineId") Integer routineId) {
        return routineService.getRoutineById(routineId);
    }

    // 루틴 수정 (PUT /api/routines/1)
    @PutMapping("/{routineId}")
    public Routine updateRoutine(@PathVariable("routineId") Integer routineId, @RequestBody Routine routineDetails) {
        return routineService.updateRoutine(routineId, routineDetails);
    }

    // 루틴 활성화/비활성화 토글 (PATCH /api/routines/{routineId}/toggle)
    @PatchMapping("/{routineId}/toggle")
    public Routine toggleRoutineActive(@PathVariable("routineId") Integer routineId) {
        return routineService.toggleRoutineActive(routineId);
    }
}