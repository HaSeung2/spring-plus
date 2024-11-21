package org.example.expert.domain.log.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LogDto {
    private final Long userId;
    private final Long todoId;
    private final Long targetId;
    private final boolean status;
    private final LocalDateTime requestTime;

    @Builder
    public LogDto(final Long userId, final Long todoId, final Long targetId, final boolean status, final LocalDateTime requestTime) {
        this.userId = userId;
        this.todoId = todoId;
        this.targetId = targetId;
        this.status = status;
        this.requestTime = requestTime;
    }
}
