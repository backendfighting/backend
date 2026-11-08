package com.fighting.goaltracker.domain.todo.repository;

import com.fighting.goaltracker.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findByUser_UserIdAndTodoDate(Integer userId, LocalDate todoDate);

    // 기간 안의 할 일 (달력의 날짜별 달성률용)
    List<Todo> findByUser_UserIdAndTodoDateBetween(Integer userId, LocalDate start, LocalDate end);

    void deleteByUser_UserId(Integer userId);

    List<Todo> findByUser_UserIdAndTodoDateAndIsCompletedFalse(Integer userId, LocalDate todoDate);

    Optional<Todo> findByTodoIdAndUser_UserId(Integer todoId, Integer userId);

}
