package org.example.expert.domain.log.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long todoId;
    private Long targetId;
    private boolean status;
    private LocalDateTime requestTime;

    @Builder
    public Log(Long userId, Long todoId, Long targetId, boolean status, LocalDateTime requestTime) {
        this.userId = userId;
        this.todoId = todoId;
        this.targetId = targetId;
        this.status = status;
        this.requestTime = requestTime;
    }

    public void updateStatus(boolean status) {
        this.status = status;
    }
}
