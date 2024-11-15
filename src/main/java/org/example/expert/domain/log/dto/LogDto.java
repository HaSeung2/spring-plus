package org.example.expert.domain.log.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LogDto {
    private final Long userId;
    private final String method;
    private final String requestUrl;
    private final LocalDateTime requestTime;

    @Builder
    public LogDto(final Long userId, final String method, final String requestUrl, final LocalDateTime requestTime) {
        this.userId = userId;
        this.method = method;
        this.requestUrl = requestUrl;
        this.requestTime = requestTime;
    }
}
