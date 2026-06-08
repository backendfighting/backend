package com.fighting.goaltracker.domain.todo.controller;

import com.fighting.goaltracker.domain.todo.dto.TodoRequestDto;
import com.fighting.goaltracker.domain.todo.dto.TodoResponseDto;
import com.fighting.goaltracker.domain.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@Tag(name = "할 일 (Todo)", description = "투두리스트 생성, 조회, 수정, 상태 변경, 삭제")
@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TodoController {

    @Autowired
    private TodoService todoService;

    // 투두 생성
    @Operation(summary = "투두 생성", description = "새로운 일회성 일정(Todo) 항목을 생성")
    @PostMapping
    public TodoResponseDto createTodo(@RequestBody TodoRequestDto request, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");

        return todoService.createTodo(userId, request);
    }

    // 날짜별 투두 조회
    @Operation(summary = "날짜별 투두 조회", description = "쿼리 파라미터로 전달된 특정 날짜의 투두리스트를 모두 조회")
    @GetMapping
    public List<TodoResponseDto> getTodosByDate(
            @RequestParam("date") LocalDate date,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return todoService.getTodosByDate(userId, date);
    }

    // 투두 완료 상태 변경
    @Operation(summary = "투두 완료 상태 변경(토글)", description = "투두의 고유 ID를 이용해 완료 여부(true/false) 상태 반전")
    @PatchMapping("/{id}/complete")
    public TodoResponseDto toggleComplete(@PathVariable("id") Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return todoService.toggleComplete(id);
    }

    // 투두 내용 수정
    @Operation(summary = "투두 내용 수정", description = "투두의 고유 ID를 이용해 할 일 내용 및 상세정보 수정")
    @PutMapping("/{id}")
    public TodoResponseDto updateTodo(@PathVariable("id") Integer id, @RequestBody TodoRequestDto request,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return todoService.updateTodo(id, request);
    }

    // 투두 삭제
    @Operation(summary = "투두 삭제", description = "투두의 고유 ID를 이용해 해당 할 일 항목 삭제")
    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable("id") Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        String title = todoService.deleteTodo(id);
        return "\"" + title + "\" 일정이 성공적으로 삭제되었습니다.";
    }
}