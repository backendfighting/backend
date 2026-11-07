package com.fighting.goaltracker.domain.user.controller;

import com.fighting.goaltracker.domain.user.dto.LoginRequestDto;
import com.fighting.goaltracker.domain.user.dto.SignupRequestDto;
import com.fighting.goaltracker.domain.user.dto.UserResponseDto;
import com.fighting.goaltracker.domain.user.dto.UpdateProfileRequestDto;
import com.fighting.goaltracker.domain.user.dto.UpdatePasswordRequestDto;
import com.fighting.goaltracker.domain.user.dto.MissionAlarmRequestDto;
import com.fighting.goaltracker.domain.user.dto.AlarmSettingsRequestDto;
import com.fighting.goaltracker.domain.user.dto.WithdrawRequestDto;

import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import com.fighting.goaltracker.global.jwt.JwtUtil;

@Tag(name = "사용자(User)", description = "회원가입, 로그인, 정보 조회 및 수정")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService; // Repository 대신 Service를 주입받음
    @Autowired
    private JwtUtil jwtUtil;

    // 회원가입 (POST /api/users/signup)
    @Operation(summary = "회원가입", description = "새로운 유저 정보를 받아 회원가입 진행")
    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequestDto request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userService.signup(user);
    }

    // 내 정보 조회 (GET /api/users/me)
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 유저의 상세 정보 조회")
    @GetMapping("/me")
    public UserResponseDto getCurrentUser(HttpServletRequest request) {
        Integer currentUserId = (Integer) request.getAttribute("userId");
        return new UserResponseDto(userService.getUserById(currentUserId));
    }

    // 로그인 (POST /api/users/login)
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 검증하여 로그인, JWT 토큰 발급")
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequestDto request) {
        User user = userService.login(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(user.getUserId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", new UserResponseDto(user));
        return response;
    }

    // 내 정보 수정 (PATCH /api/users/me)
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 유저의 프로필 정보 수정")
    @PatchMapping("/me")
    public UserResponseDto updateProfile(@RequestBody UpdateProfileRequestDto request, HttpServletRequest httpRequest) {
        Integer currentUserId = (Integer) httpRequest.getAttribute("userId");
        return new UserResponseDto(userService.updateProfile(currentUserId, request));
    }

    // 비밀번호 변경 (PUT /api/users/password)
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 유저의 비밀번호 변경")
    @PutMapping("/password")
    public String updatePassword(@RequestBody UpdatePasswordRequestDto request, HttpServletRequest httpRequest) {
        Integer currentUserId = (Integer) httpRequest.getAttribute("userId");

        userService.updatePassword(currentUserId, request.getCurrentPassword(), request.getNewPassword());
        return "비밀번호가 성공적으로 변경되었습니다.";
    }

    // 오늘의 미션 알림 시간 변경 (PUT /api/users/me/alarm-times)
    @Operation(summary = "오늘의 미션 알림 시간 변경", description = "times에 \"HH:mm\" 목록을 보내면 저장. 빈 목록이면 알림 끄기")
    @PutMapping("/me/alarm-times")
    public UserResponseDto updateMissionAlarmTimes(@RequestBody MissionAlarmRequestDto request,
            HttpServletRequest httpRequest) {
        Integer currentUserId = (Integer) httpRequest.getAttribute("userId");
        return new UserResponseDto(userService.updateMissionAlarmTimes(currentUserId, request.getTimes()));
    }

    // 알림 설정 변경 (PATCH /api/users/me/alarm-settings) - 보낸 항목만 바뀜
    @Operation(summary = "알림 설정 변경", description = "기기 알림(전체 스위치), 오늘의 미션/목표 리마인더 알림 켜기·끄기, 오늘의 미션 알림 시간, 목표 리마인더 시간. 보낸 항목만 변경")
    @PatchMapping("/me/alarm-settings")
    public UserResponseDto updateAlarmSettings(@RequestBody AlarmSettingsRequestDto request,
            HttpServletRequest httpRequest) {
        Integer currentUserId = (Integer) httpRequest.getAttribute("userId");
        return new UserResponseDto(userService.updateAlarmSettings(currentUserId, request));
    }

    // 회원 탈퇴 (DELETE /api/users/me, body: {"password": "..."})
    // 비밀번호가 맞을 때만 계정과 모든 데이터를 삭제한다
    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 현재 로그인한 유저의 계정 및 모든 데이터 삭제")
    @DeleteMapping("/me")
    public String deleteUser(@RequestBody(required = false) WithdrawRequestDto body, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        userService.deleteUser(userId, body == null ? null : body.getPassword());
        return "회원 탈퇴가 완료되었습니다.";
    }
}