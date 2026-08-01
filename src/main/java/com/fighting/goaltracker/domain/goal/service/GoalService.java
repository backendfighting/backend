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
                goal.setDescription(request.getDescription());
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
}