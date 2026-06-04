package com.fighting.goaltracker.domain.user.controller;

import com.fighting.goaltracker.domain.user.dto.LoginRequestDto;
import com.fighting.goaltracker.domain.user.dto.SignupRequestDto;

import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

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
    public User signup(@RequestBody SignupRequestDto request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
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
    public User login(@RequestBody LoginRequestDto request, HttpSession session) {
        User user = userService.login(request.getEmail(), request.getPassword());
        session.setAttribute("userId", user.getUserId());
        return user;
    }

    // 로그아웃 (POST /api/users/logout)
    @Operation(summary = "로그아웃", description = "현재 로그인한 유저의 세션을 삭제하여 로그아웃 처리")
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 전체 삭제
        return "로그아웃 되었습니다.";
    }

    // 내 정보 수정 (PATCH /api/users/me)
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 유저의 프로필 정보 수정")
    @PatchMapping("/me")
    public User updateProfile(@RequestBody User updateRequest, HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId"); // 세션에서 userId 꺼내기
        return userService.updateProfile(currentUserId, updateRequest);
    }

    // 비밀번호 변경 (PUT /api/users/password)
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 유저의 비밀번호 변경")
    @PutMapping("/password")
    public String updatePassword(@RequestBody Map<String, String> passwordRequest, HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        String currentPassword = passwordRequest.get("currentPassword");
        String newPassword = passwordRequest.get("newPassword");
        userService.updatePassword(currentUserId, currentPassword, newPassword);
        return "비밀번호가 성공적으로 변경되었습니다.";
    }
}