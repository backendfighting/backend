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
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", allowedHeaders = "*")
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

    // 내 정보 조회 (GET /api/users/me)
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 유저의 상세 정보 조회")
    @GetMapping("/me")
    public User getCurrentUser(HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        if (currentUserId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return userService.getUserById(currentUserId);
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
        if (currentUserId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        return userService.updateProfile(currentUserId, updateRequest);
    }

    // 비밀번호 변경 (PUT /api/users/password)
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 유저의 비밀번호 변경")
    @PutMapping("/password")
    public String updatePassword(@RequestBody Map<String, String> passwordRequest, HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        if (currentUserId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");

        String currentPassword = passwordRequest.get("currentPassword");
        String newPassword = passwordRequest.get("newPassword");
        userService.updatePassword(currentUserId, currentPassword, newPassword);
        return "비밀번호가 성공적으로 변경되었습니다.";
    }

    // 회원 탈퇴 (DELETE /api/users/me)
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 유저의 계정 및 모든 데이터 삭제")
    @DeleteMapping("/me")
    public String deleteUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null)
            throw new IllegalArgumentException("로그인이 필요합니다.");
        userService.deleteUser(userId);
        session.invalidate(); // 세션도 같이 삭제
        return "회원 탈퇴가 완료되었습니다.";
    }
}