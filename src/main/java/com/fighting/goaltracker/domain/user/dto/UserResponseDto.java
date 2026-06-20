package com.fighting.goaltracker.domain.user.dto;

import com.fighting.goaltracker.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Integer userId;
    private String name;
    private String email;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}