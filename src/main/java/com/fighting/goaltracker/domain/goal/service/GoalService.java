package com.fighting.goaltracker.domain.goal.service;

import com.fighting.goaltracker.domain.goal.dto.GoalCompleteRequestDto;
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

        // 목표 완료 처리
        @Transactional
        public Goal completeGoal(Integer goalId, Integer userId, GoalCompleteRequestDto request) {
                Goal goal = goalRepository.findByGoalIdAndUser_UserId(goalId, userId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));
                String status = request.getStatus();

                if (status == null || (!status.equals("성공") && !status.equals("실패"))) {
                        throw new IllegalArgumentException("status는 '성공' 또는 '실패'만 가능합니다.");
                }

                // 실패일 때는 원인 필수 선택
                if (status.equals("실패") && (request.getReason() == null || request.getReason().trim().isEmpty())) {
                        throw new IllegalArgumentException("실패 원인을 입력해주세요.");
                }

                goal.setStatus(status);
                goal.setReason(status.equals("실패") ? request.getReason() : null);

                return goalRepository.save(goal);
        }

        // 목표 수정
        @Transactional
        public Goal updateGoal(Integer goalId, Integer userId, GoalRequestDto request) {
                Goal goal = goalRepository.findByGoalIdAndUser_UserId(goalId, userId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));

                goal.update(request.getTitle(), request.getCategory(), request.getDescription(),
                                request.getStartDate(), request.getEndDate(),
                                request.getProgress(), request.getStatus(), request.getReason());

                return goalRepository.save(goal);
        }

        // 목표 삭제
        @Transactional
        public void deleteGoal(Integer goalId, Integer userId) {
                Goal goal = goalRepository.findByGoalIdAndUser_UserId(goalId, userId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));
                goalRepository.delete(goal);
        }
}