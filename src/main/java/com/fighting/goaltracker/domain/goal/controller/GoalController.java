package com.fighting.goaltracker.domain.goal.controller;

import com.fighting.goaltracker.domain.goal.dto.GoalRequestDto;
import com.fighting.goaltracker.domain.goal.dto.GoalResponseDto;
import com.fighting.goaltracker.domain.goal.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "목표(Goal)", description = "목표 생성, 조회 관련 API")
@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    // 목표 추가 (POST /api/goals)
    @Operation(summary = "목표 생성", description = "새로운 목표를 생성")
    @PostMapping
    public GoalResponseDto createGoal(@RequestBody GoalRequestDto request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        return new GoalResponseDto(goalService.createGoal(userId, request));
    }

    // 목표 목록 조회 (GET /api/goals)
    @Operation(summary = "목표 목록 조회", description = "로그인한 유저의 전체 목표 목록 조회")
    @GetMapping
    public List<GoalResponseDto> getGoals(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        return goalService.getGoalsByUser(userId)
                .stream()
                .map(GoalResponseDto::new)
                .collect(Collectors.toList());
    }

    // 목표 수정 (PUT /api/goals/{goalId})
    @Operation(summary = "목표 수정", description = "목표 고유 ID를 이용해 목표 정보 수정")
    @PutMapping("/{goalId}")
    public GoalResponseDto updateGoal(@PathVariable("goalId") Integer goalId, @RequestBody GoalRequestDto request) {
        return new GoalResponseDto(goalService.updateGoal(goalId, request));
    }

    // 목표 삭제 (DELETE /api/goals/{goalId})
    @Operation(summary = "목표 삭제", description = "목표 고유 ID를 이용해 목표 삭제")
    @DeleteMapping("/{goalId}")
    public String deleteGoal(@PathVariable("goalId") Integer goalId) {
        goalService.deleteGoal(goalId);
        return "목표가 성공적으로 삭제되었습니다.";
    }
}