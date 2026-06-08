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
        Integer userId = todo.getUser().getUserId();

        // 제목 + 날짜 + 시간이 같은 투두 확인
        if (todo.getTodoTime() != null) {
            // 제목 + 날짜 + 시간이 같은 투두 확인
            List<Todo> sameTitle = todoRepository.findByUser_UserIdAndTitleAndTodoDateAndTodoTime(
                    userId, todo.getTitle(), todo.getTodoDate(), todo.getTodoTime());
            if (!sameTitle.isEmpty()) {
                throw new IllegalArgumentException("이미 같은 일정이 존재합니다.");
            }

            // 날짜 + 시간이 같은 투두 확인
            List<Todo> sameTime = todoRepository.findByUser_UserIdAndTodoDateAndTodoTime(
                    userId, todo.getTodoDate(), todo.getTodoTime());
            if (!sameTime.isEmpty()) {
                throw new IllegalArgumentException("동일한 시간에 일정이 존재합니다.");
            }
        }
        return todoRepository.save(todo);
    }

    // 날짜별 투두 조회
    public List<Todo> getTodosByDate(Integer userId, String dateStr) {
        LocalDate localDate = LocalDate.parse(dateStr);
        return todoRepository.findByUser_UserIdAndTodoDate(userId, localDate);
    }

    // 투두 완료 상태 변경 (토글)
    @Transactional
    public Todo toggleComplete(Integer id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 투두를 찾을 수 없습니다."));

        // 현재 상태 반전시키기
        todo.setCompleted(!todo.isCompleted());
        return todoRepository.save(todo);
    }

    // 투두 내용 수정
    @Transactional
    public Todo updateTodo(Integer id, Todo todoDetails) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 투두를 찾을 수 없습니다."));

        if (todoDetails.getTitle() != null) {
            todo.setTitle(todoDetails.getTitle());
        }
        if (todoDetails.getDescription() != null) {
            todo.setDescription(todoDetails.getDescription());
        }
        if (todoDetails.getTodoDate() != null) {
            todo.setTodoDate(todoDetails.getTodoDate());
        }

        return todoRepository.save(todo);
    }

    // 투두 삭제
    @Transactional
    public String deleteTodo(Integer id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 투두를 찾을 수 없습니다."));

        String title = todo.getTitle(); // 삭제 전에 제목 저장
        todoRepository.delete(todo);
        return title; // 제목 반환 (service에서 삭제 안내 문구 출력 위해서)
    }
}
