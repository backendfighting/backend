package com.fighting.goaltracker.domain.goal.service;

import com.fighting.goaltracker.domain.goal.dto.GoalRequestDto;
import com.fighting.goaltracker.domain.goal.entity.Goal;
import com.fighting.goaltracker.domain.goal.repository.GoalRepository;
import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    // 목표 생성
    @Transactional
    public Goal createGoal(Integer userId, GoalRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle(request.getTitle());
        goal.setCategory(request.getCategory());
        goal.setDesc(request.getDesc());
        goal.setStartDate(request.getStartDate());
        goal.setEndDate(request.getEndDate());
        goal.setProgress(request.getProgress() != null ? request.getProgress() : 0);
        goal.setStatus(request.getStatus() != null ? request.getStatus() : "진행중");
        goal.setReason(request.getReason());

        return goalRepository.save(goal);
    }

    // 유저의 전체 목표 조회
    @Transactional(readOnly = true)
    public List<Goal> getGoalsByUser(Integer userId) {
        return goalRepository.findByUser_UserId(userId);
    }

    // 🌟 1. 목표 상태 업데이트 기능 (성공/실패 처리)
    @Transactional
    public Goal updateGoalStatus(Integer userId, Integer goalId, String status, Integer progress, String reason) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다. ID: " + goalId));

        if (!goal.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("이 목표를 수정할 권한이 없습니다.");
        }

        if (status != null) goal.setStatus(status);
        if (progress != null) goal.setProgress(progress);
        if (reason != null) goal.setReason(reason);

        return goalRepository.save(goal);
    }

    // 🌟 2. 목표 삭제 기능
    @Transactional
    public void deleteGoal(Integer userId, Integer goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다. ID: " + goalId));

        if (!goal.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("이 목표를 삭제할 권한이 없습니다.");
        }

        goalRepository.delete(goal);
    }
}