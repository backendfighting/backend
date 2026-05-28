package com.fighting.goaltracker.domain.user.controller;

import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*") // 프론트 협업용 CORS 임시 전면 허용
public class UserController {

    @Autowired
    private UserService userService; // Repository 대신 Service를 주입받음

    // 회원가입 (POST /api/users/signup)
    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.signup(user);
    }

    // 사용자 조회 (ID로 찾기) (GET /api/users/1)
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    // 로그인 (POST /api/users/login)
    @PostMapping("/login")
    public User login(@RequestBody User loginRequest) {
        // Service에 이메일과 비밀번호를 넘겨서 인증 결과를 받음
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    // 내 정보 수정 (PATCH /api/users/me)
    @PatchMapping("/me")
    public User updateProfile(@RequestBody User updateRequest) {
        return userService.updateProfile(updateRequest);
    }

    // 비밀번호 변경 (PUT /api/users/password)
    @PutMapping("/password")
    public String updatePassword(@RequestBody Map<String, String> passwordRequest) {
        String newPassword = passwordRequest.get("newPassword");
        userService.updatePassword(newPassword);
        return "비밀번호가 성공적으로 변경되었습니다.";
    }
}