package com.fighting.goaltracker.domain.todo.service;

import com.fighting.goaltracker.domain.todo.dto.TodoRequestDto;
import com.fighting.goaltracker.domain.todo.dto.TodoResponseDto;
import com.fighting.goaltracker.domain.todo.entity.Todo;
import com.fighting.goaltracker.domain.todo.repository.TodoRepository;
import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // 투두 생성
    @Transactional
    public TodoResponseDto createTodo(Integer userId, TodoRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 제목 + 날짜 + 시간이 같은 투두 확인
        if (request.getTodoTime() != null) {
            // 제목 + 날짜 + 시간이 같은 투두 확인
            List<Todo> sameTitle = todoRepository.findByUser_UserIdAndTitleAndTodoDateAndTodoTime(
                    userId, request.getTitle(), request.getTodoDate(), request.getTodoTime());
            if (!sameTitle.isEmpty()) {
                throw new IllegalArgumentException("이미 같은 일정이 존재합니다.");
            }

            // 날짜 + 시간이 같은 투두 확인
            List<Todo> sameTime = todoRepository.findByUser_UserIdAndTodoDateAndTodoTime(
                    userId, request.getTodoDate(), request.getTodoTime());
            if (!sameTime.isEmpty()) {
                throw new IllegalArgumentException("동일한 시간에 일정이 존재합니다.");
            }
        }

        Todo todo = Todo.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .todoDate(request.getTodoDate())
                .todoTime(request.getTodoTime())
                .priority(request.getPriority())
                .build();

        return new TodoResponseDto(todoRepository.save(todo));
    }

    // 날짜별 투두 조회
    public List<TodoResponseDto> getTodosByDate(Integer userId, LocalDate date) {
        List<Todo> todo = todoRepository.findByUser_UserIdAndTodoDate(userId, date);

        return todo.stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    // 투두 완료 상태 변경 (토글)
    @Transactional
    public TodoResponseDto toggleComplete(Integer id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 투두를 찾을 수 없습니다."));

        todo.toggleComplete();
        return new TodoResponseDto(todoRepository.save(todo));
    }

    // 투두 내용 수정
    @Transactional
    public TodoResponseDto updateTodo(Integer id, TodoRequestDto request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 투두를 찾을 수 없습니다."));

        todo.update(request.getTitle(), request.getDescription(),
                request.getTodoDate(), request.getTodoTime(), request.getPriority());

        return new TodoResponseDto(todoRepository.save(todo));
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
