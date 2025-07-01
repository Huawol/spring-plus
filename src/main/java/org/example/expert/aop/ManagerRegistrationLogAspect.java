package org.example.expert.aop;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.aop.service.LogService;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class ManagerRegistrationLogAspect {

    private final LogService logService;
    private final HttpServletRequest request; // 요청 정보 추출을 위해 필요

    // 매니저 등록 메서드 실행 "직후" 자동 실행
    @After("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))")
    public void logManagerRegistration(JoinPoint joinPoint) {
        // 예시: 첫 번째 파라미터로 User 객체가 들어온다고 가정
        Object[] args = joinPoint.getArgs();
        String nickname = null;
        for (Object arg : args) {
            if (arg instanceof AuthUser) {
                nickname = ((AuthUser) arg).getNickname();
                break;
            }
        }
        String requestUrl = request.getRequestURI();
        LocalDateTime now = LocalDateTime.now();
        String method = joinPoint.getSignature().getName();

        // 로그 저장 (항상 별도 트랜잭션)
        logService.saveLog(nickname, now, requestUrl, method);
    }
}