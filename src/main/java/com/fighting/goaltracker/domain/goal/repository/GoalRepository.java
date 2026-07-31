package com.fighting.goaltracker.domain.goal.repository;

import com.fighting.goaltracker.domain.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Integer> {
    List<Goal> findByUser_UserId(Integer userId);
}