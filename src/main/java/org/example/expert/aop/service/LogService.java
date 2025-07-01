package org.example.expert.aop.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.aop.entity.Log;
import org.example.expert.aop.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String nickname, LocalDateTime requestTime, String requestUrl, String method) {
        Log log = new Log(nickname, requestTime, requestUrl, method);
        logRepository.save(log);
    }
}
