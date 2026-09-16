package com.fighting.goaltracker.domain.notification.controller;

import com.fighting.goaltracker.domain.notification.dto.NotificationResponseDto;
import com.fighting.goaltracker.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알림(Notification)", description = "알림 조회 및 읽음 처리")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 알림 목록 조회
    @Operation(summary = "알림 목록 조회", description = "로그인한 유저의 전체 알림을 최신순으로 조회")
    @GetMapping
    public List<NotificationResponseDto> getNotifications(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return notificationService.getNotifications(userId);
    }

    // 알림 읽음 처리
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경")
    @PatchMapping("/{id}/read")
    public String markAsRead(@PathVariable("id") Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        notificationService.markAsRead(id, userId);
        return "읽음 처리되었습니다.";
    }
}