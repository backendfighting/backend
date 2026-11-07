package com.fighting.goaltracker.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

// 회원 탈퇴할 때 본인 확인용 비밀번호
@Getter
@Setter
public class WithdrawRequestDto {
    private String password;
}
