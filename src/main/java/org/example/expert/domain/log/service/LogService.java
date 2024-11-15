package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.dto.LogDto;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    public void saveLog(LogDto logDto){
        Log log = Log.builder()
            .userId(logDto.getUserId())
            .method(logDto.getMethod())
            .requestUrl(logDto.getRequestUrl())
            .requestTime(logDto.getRequestTime())
            .build();
        logRepository.save(log);
    }
}
