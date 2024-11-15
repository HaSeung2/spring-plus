package org.example.expert.domain.todo.dto.request;

import lombok.Getter;

@Getter
public class TodoQueryDslSearchRequest {
    private String keyword;
    private String startDate;
    private String endDate;
    private String nickName;
}
