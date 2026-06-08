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

    // 제목 + 날짜 + 시간이 같은 투두 확인
    List<Todo> findByUser_UserIdAndTitleAndTodoDateAndTodoTime(Integer userId, String title, LocalDate todoDate,
            LocalTime todoTime);

    // 날짜 + 시간이 같은 투두 확인
    List<Todo> findByUser_UserIdAndTodoDateAndTodoTime(Integer userId, LocalDate todoDate, LocalTime todoTime);

}
