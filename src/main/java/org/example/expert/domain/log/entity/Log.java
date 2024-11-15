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
    private String method;
    private String requestUrl;
    private LocalDateTime requestTime;

    @Builder
    public Log(Long userId, String method, String requestUrl, LocalDateTime requestTime) {
        this.userId = userId;
        this.method = method;
        this.requestUrl = requestUrl;
        this.requestTime = requestTime;
    }
}
