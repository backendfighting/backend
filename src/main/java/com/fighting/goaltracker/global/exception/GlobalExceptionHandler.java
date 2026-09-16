package com.fighting.goaltracker.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 공통 응답 생성
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    // 비즈니스 로직에서 던지는 잘못된 요청 (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // JSON 형식 오류 (400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다.");
    }

    // 필수 파라미터 누락 (400)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException e) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "필수 파라미터가 누락되었습니다: " + e.getParameterName());
    }

    // 파라미터 타입 불일치 (400) - 예: date=오늘
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "파라미터 형식이 올바르지 않습니다: " + e.getName());
    }

    // DB 제약 조건 위반 (409) - 예: 이메일 중복
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException e) {
        // 실제 원인은 서버 로그에만 기록하고, 사용자에게는 일반화된 메시지만 전달
        System.out.println("[DataIntegrityViolation] " + e.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "이미 사용 중이거나 처리할 수 없는 데이터입니다.");
    }

    // 그 외 예상하지 못한 서버 오류 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception e) {
        // 상세 내용은 서버 로그에만 남기고, 사용자에게는 노출하지 않음
        e.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
    }
}