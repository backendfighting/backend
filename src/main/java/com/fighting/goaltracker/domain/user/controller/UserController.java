package com.fighting.goaltracker.domain.user.controller;

import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자(User)", description = "회원가입, 로그인, 정보 조회 및 수정")
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*") // 프론트 협업용 CORS 임시 전면 허용
public class UserController {

    @Autowired
    private UserService userService; // Repository 대신 Service를 주입받음

    // 회원가입 (POST /api/users/signup)
    @Operation(summary = "회원가입", description = "새로운 유저 정보를 받아 회원가입 진행")
    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.signup(user);
    }

    // 사용자 조회 (ID로 찾기) (GET /api/users/1)
    @Operation(summary = "사용자 조회(ID)", description = "유저의 고유 식별자(ID)를 이용해 해당 사용자의 상세 정보를 조회")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    // 로그인 (POST /api/users/login)
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 검증하여 로그인")
    @PostMapping("/login")
    public User login(@RequestBody User loginRequest) {
        // Service에 이메일과 비밀번호를 넘겨서 인증 결과를 받음
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    // 내 정보 수정 (PATCH /api/users/me)
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 유저의 프로필 정보 수정 (현재는 1번 유저로 고정)")
    @PatchMapping("/me")
    public User updateProfile(@RequestBody User updateRequest) {
        Integer currentUserId = 1;
        return userService.updateProfile(currentUserId, updateRequest);
    }

    // 비밀번호 변경 (PUT /api/users/password)
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 유저의 비밀번호 변경 (현재는 1번 유저로 고정)")
    @PutMapping("/password")
    public String updatePassword(@RequestBody Map<String, String> passwordRequest) {
        // 임시 아이디 1로 지정
        Integer currentUserId = 1;

        String newPassword = passwordRequest.get("newPassword");

        // 서비스에 id와 새 비밀번호 둘 다 넘겨줌
        userService.updatePassword(currentUserId, newPassword);

        return "비밀번호가 성공적으로 변경되었습니다.";
    }
}