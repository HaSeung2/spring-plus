package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.dto.LogDto;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Log saveLog(LogDto logDto){
        Log log = Log.builder()
            .userId(logDto.getUserId())
            .todoId(logDto.getTodoId())
            .targetId(logDto.getTargetId())
            .requestTime(logDto.getRequestTime())
            .status(logDto.isStatus())
            .build();
        return logRepository.save(log);
    }

    public void updateStatus(Log log){
        logRepository.save(log);
    }
}
