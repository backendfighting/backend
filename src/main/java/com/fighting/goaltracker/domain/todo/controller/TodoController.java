package com.fighting.goaltracker.domain.todo.controller;

import com.fighting.goaltracker.domain.todo.entity.Todo;
import com.fighting.goaltracker.domain.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "할 일 (Todo)", description = "투두리스트 생성, 조회, 수정, 상태 변경, 삭제")
@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TodoController {

    @Autowired
    private TodoService todoService;

    // 투두 생성 (POST /api/todos)
    @Operation(summary = "투두 생성", description = "새로운 할 일(Todo) 항목을 생성")
    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    // 날짜별 투두 조회 (GET /api/todos?date=2026-05-07)
    @Operation(summary = "날짜별 투두 조회", description = "쿼리 파라미터로 전달된 특정 날짜의 투두리스트를 모두 조회")
    @GetMapping
    public List<Todo> getTodosByDate(
            @RequestParam("userId") Integer userId,
            @RequestParam("date") String date) {
        return todoService.getTodosByDate(userId, date);
    }

    // 투두 완료 상태 변경 (PATCH /api/todos/1/complete)
    @Operation(summary = "투두 완료 상태 변경(토글)", description = "투두의 고유 ID를 이용해 완료 여부(true/false) 상태 반전")
    @PatchMapping("/{id}/complete")
    public Todo toggleComplete(@PathVariable("id") Integer id) {
        return todoService.toggleComplete(id);
    }

    // 투두 내용 수정 (PUT /api/todos/1)
    @Operation(summary = "투두 내용 수정", description = "투두의 고유 ID를 이용해 할 일 내용 및 상세정보 수정")
    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable("id") Integer id, @RequestBody Todo todoDetails) {
        return todoService.updateTodo(id, todoDetails);
    }

    // 투두 삭제 (DELETE /api/todos/1)
    @Operation(summary = "투두 삭제", description = "투두의 고유 ID를 이용해 해당 할 일 항목 삭제")
    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable("id") Integer id) {
        todoService.deleteTodo(id);
        return "성공적으로 삭제되었습니다. ID: " + id;
    }
}