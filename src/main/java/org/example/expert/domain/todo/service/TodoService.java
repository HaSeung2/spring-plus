package org.example.expert.domain.todo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoQueryDslSearchRequest;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoQueryDslSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// 1번 문제 readOnly true 로 되어 있어서 에러 발생
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(User user, TodoSaveRequest todoSaveRequest) {
        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail(),
                                 user.getNickName())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, TodoSearchRequest todoSearchRequest) {
        Pageable pageable = PageRequest.of(page - 1, size);

        if(todoSearchRequest.getWeather() != null && (todoSearchRequest.getStartDate() == null && todoSearchRequest.getEndDate() == null)){
            return todoRepository.findByWeather(todoSearchRequest.getWeather(), pageable).map(TodoResponse::new);
        }
        if(todoSearchRequest.getWeather() == null && (todoSearchRequest.getStartDate() != null && todoSearchRequest.getEndDate() != null)){
            return todoRepository.findByModifyAt(parseLocalDateTime(todoSearchRequest.getStartDate()), parseLocalDateTime(todoSearchRequest.getEndDate()), pageable).map(TodoResponse::new);
        }
        if(todoSearchRequest.getWeather() != null && (todoSearchRequest.getStartDate() != null && todoSearchRequest.getEndDate() != null)){
            return todoRepository.findByWeatherAndModifyAt(todoSearchRequest.getWeather() , parseLocalDateTime(todoSearchRequest.getStartDate()), parseLocalDateTime(todoSearchRequest.getEndDate()), pageable).map(TodoResponse::new);
        }

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(), todo.getUser().getNickName()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public Page<TodoQueryDslSearchResponse> searchTodos(
        int page,
        int size,
        TodoQueryDslSearchRequest todoQueryDslSearchRequest) {
        LocalDateTime startDate = todoQueryDslSearchRequest.getStartDate() != null ? parseLocalDateTime(todoQueryDslSearchRequest.getStartDate()) : null;
        LocalDateTime endDate = todoQueryDslSearchRequest.getEndDate() != null ? parseLocalDateTime(todoQueryDslSearchRequest.getEndDate()) : null;
        Pageable pageable = PageRequest.of(page - 1, size);
        return todoRepository.searchTodo(todoQueryDslSearchRequest.getKeyword(), startDate, endDate,
                                         todoQueryDslSearchRequest.getNickName(), pageable);
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail(),
                                 user.getNickName()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    private LocalDateTime parseLocalDateTime(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay();
    }
}
