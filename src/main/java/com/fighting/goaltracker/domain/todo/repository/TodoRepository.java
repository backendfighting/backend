package com.fighting.goaltracker.domain.todo.repository;

import com.fighting.goaltracker.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findByTodoDate(LocalDate todoDate);
}