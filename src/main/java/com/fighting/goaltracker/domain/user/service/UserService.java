package com.fighting.goaltracker.domain.user.service;

import com.fighting.goaltracker.domain.user.entity.User;
import com.fighting.goaltracker.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 회원가입
    @Transactional
    public User signup(User user) {
        // 중복 이메일 확인
        userRepository.findByEmail(user.getEmail())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("이미 가입된 이메일 주소입니다.");
                });

        // 중복이 없다면 정상적으로 저장
        return userRepository.save(user);
    }

    // 사용자 조회 (ID로 찾기)
    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 로그인 인증
    @Transactional(readOnly = true)
    public User login(String email, String password) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호가 일치하는지 확인 (현재는 암호화 없이 plain text 비교)
        if (user.getPassword().equals(password)) {
            return user;
        } else {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // 내 정보 수정
    @Transactional
    public User updateProfile(Integer currentUserId, User updateRequest) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이름이 빈 문자열로 들어오면 에러
        if (updateRequest.getName() != null && updateRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 비워둘 수 없습니다.");
        }

        // 이메일이 빈 문자열로 들어오면 에러
        if (updateRequest.getEmail() != null && updateRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 비워둘 수 없습니다.");
        }

        // 값이 있을 때만 변경
        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }

        return userRepository.save(user);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(Integer currentUserId, String currentPassword, String newPassword) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!user.getPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(newPassword);
        userRepository.save(user);
    }
}