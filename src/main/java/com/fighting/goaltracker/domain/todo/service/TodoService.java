package com.fighting.goaltracker.domain.todo.service;

import com.fighting.goaltracker.domain.todo.entity.Todo;
import com.fighting.goaltracker.domain.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    // 투두 생성
    @Transactional
    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    // 날짜별 투두 조회
    @Transactional(readOnly = true)
    public List<Todo> getTodosByDate(String dateStr) {
        LocalDate localDate = LocalDate.parse(dateStr);
        return todoRepository.findByTodoDate(localDate);
    }

    // 투두 완료 상태 변경 (토글)
    @Transactional
    public Todo toggleComplete(Integer id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 투두를 찾을 수 없습니다."));

        // 현재 상태 반전시키기
        todo.setCompleted(!todo.isCompleted());
        return todoRepository.save(todo);
    }

    // 투두 내용 수정
    @Transactional
    public Todo updateTodo(Integer id, Todo todoDetails) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 투두를 찾을 수 없습니다."));

        // 제목, 내용, 날짜 정보 수정
        todo.setTitle(todoDetails.getTitle());
        todo.setDescription(todoDetails.getDescription());
        todo.setTodoDate(todoDetails.getTodoDate());

        return todoRepository.save(todo);
    }

    // 투두 삭제
    @Transactional
    public void deleteTodo(Integer id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 투두를 찾을 수 없습니다."));

        todoRepository.delete(todo);
    }
}
