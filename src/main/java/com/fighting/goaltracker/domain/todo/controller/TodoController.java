package com.fighting.goaltracker.domain.todo.controller;

import com.fighting.goaltracker.domain.todo.entity.Todo;
import com.fighting.goaltracker.domain.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TodoController {

    @Autowired
    private TodoService todoService;

    // 투두 생성 (POST /api/todos)
    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    // 날짜별 투두 조회 (GET /api/todos?date=2026-05-07)
    @GetMapping
    public List<Todo> getTodosByDate(@RequestParam("date") String date) {
        return todoService.getTodosByDate(date);
    }

    // 투두 완료 상태 변경 (PATCH /api/todos/1/complete)
    @PatchMapping("/{id}/complete")
    public Todo toggleComplete(@PathVariable("id") Integer id) {
        return todoService.toggleComplete(id);
    }

    // 투두 내용 수정 (PUT /api/todos/1)
    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable("id") Integer id, @RequestBody Todo todoDetails) {
        return todoService.updateTodo(id, todoDetails);
    }

    // 투두 삭제 (DELETE /api/todos/1)
    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable("id") Integer id) {
        todoService.deleteTodo(id);
        return "성공적으로 삭제되었습니다. ID: " + id;
    }
}