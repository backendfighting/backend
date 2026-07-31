package com.fighting.goaltracker.domain.goal.controller;

import com.fighting.goaltracker.domain.goal.dto.GoalRequestDto;
import com.fighting.goaltracker.domain.goal.entity.Goal;
import com.fighting.goaltracker.domain.goal.service.GoalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}