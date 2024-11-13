package org.example.expert.domain.todo.dto.request;

import lombok.Getter;

@Getter
public class TodoSearchRequest {
    private String weather;
    private String startDate;
    private String endDate;
}
