package com.fighting.goaltracker.domain.goal.controller;

import com.fighting.goaltracker.domain.goal.dto.GoalRequestDto;
import com.fighting.goaltracker.domain.goal.entity.Goal;
import com.fighting.goaltracker.domain.goal.service.GoalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    // 1. 목표 추가 API (POST)
    @PostMapping
    public Goal createGoal(@RequestBody GoalRequestDto request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) throw new IllegalArgumentException("로그인이 필요합니다.");
        return goalService.createGoal(userId, request);
    }

    // 2. 목표 목록 불러오기 API (GET)
    @GetMapping
    public List<Goal> getGoals(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) throw new IllegalArgumentException("로그인이 필요합니다.");
        return goalService.getGoalsByUser(userId);
    }

    // 🌟 3. 목표 상태/결과 업데이트 API (PATCH)
    @PatchMapping("/{goalId}/status")
    public Goal updateGoalStatus(
            @PathVariable Integer goalId,
            @RequestBody Map<String, Object> updates,
            HttpServletRequest httpRequest) {
        
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) throw new IllegalArgumentException("로그인이 필요합니다.");

        String status = (String) updates.get("status");
        Integer progress = (Integer) updates.get("progress");
        String reason = (String) updates.get("reason");

        return goalService.updateGoalStatus(userId, goalId, status, progress, reason);
    }

    // 🌟 4. 목표 삭제 API (DELETE)
    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable Integer goalId, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) throw new IllegalArgumentException("로그인이 필요합니다.");
        
        goalService.deleteGoal(userId, goalId);
    }
}