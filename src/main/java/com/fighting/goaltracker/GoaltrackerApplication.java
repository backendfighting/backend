package com.fighting.goaltracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GoaltrackerApplication {

	public static void main(String[] args) {
		// EC2 기본 시간대(UTC)와 상관없이 생성 시각·오늘 날짜를 한국 시간으로 맞춘다
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(GoaltrackerApplication.class, args);
	}

}
