package org.example.expert.aop.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "log")
@Getter
@NoArgsConstructor
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;         // 관리자 아이디
    private LocalDateTime requestTime; // 요청 시간
    private String requestUrl;     // 요청 경로
    private String method;         // 호출 메서드

    public Log(String nickname, LocalDateTime requestTime, String requestUrl, String method) {
        this.nickname = nickname;
        this.requestTime = requestTime;
        this.requestUrl = requestUrl;
        this.method = method;
    }
}
