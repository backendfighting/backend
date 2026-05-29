package com.fighting.goaltracker.domain.user.repository;

import com.fighting.goaltracker.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 유저 정보를 찾는 메서드 (Spring Data JPA가 자동으로 쿼리 생성)
    Optional<User> findByEmail(String email);
}