package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.expert.domain.todo.dto.response.TodoQueryDslSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomTodoRepository {
    Optional<Todo> findByIdWithUser(Long todoId);

    Page<TodoQueryDslSearchResponse> searchTodo( String keyword,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String nickName,
        Pageable pageable);
}
