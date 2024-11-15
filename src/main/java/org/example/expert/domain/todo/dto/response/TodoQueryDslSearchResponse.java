package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoQueryDslSearchResponse {
    private String title;
    private Long mangerCnt;
    private Long commentCnt;

    public TodoQueryDslSearchResponse(String title, Long mangerCnt, Long commentCnt){
        this.title = title;
        this.mangerCnt = mangerCnt;
        this.commentCnt = commentCnt;
    }
}
