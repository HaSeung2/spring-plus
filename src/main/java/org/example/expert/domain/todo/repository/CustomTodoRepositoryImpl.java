package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoQueryDslSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomTodoRepositoryImpl implements CustomTodoRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QTodo todo = QTodo.todo;
    private final QManager manager = QManager.manager;
    private final QComment comment = QComment.comment;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(todo)
                                       .where(todo.id.eq(todoId))
                                       .leftJoin(todo.user)
                                       .fetchJoin()
                                       .fetchFirst()
        );
    }

    public Page<TodoQueryDslSearchResponse> searchTodo(
        String keyword,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String nickName,
        Pageable pageable) {

        List<TodoQueryDslSearchResponse> list = jpaQueryFactory.select(Projections.fields(TodoQueryDslSearchResponse.class,
                                                                     todo.title,
                                                                            manager.id.count().as("mangerCnt"),
                                                                            comment.id.count().as("commentCnt")
                                                                    ))
                                                                        .from(todo)
                                                                        .leftJoin(todo.managers, manager).on(manager.todo.id.eq(todo.id))
                                                                        .leftJoin(todo.comments, comment).on(comment.todo.id.eq(todo.id))
                                                                        .where(keywordEq(keyword), dateEq(startDate, endDate), nickNameEq(nickName))
                                                                        .orderBy(todo.createdAt.desc())
                                                                        .groupBy(todo.id)
                                                                        .offset(pageable.getOffset())
                                                                        .limit(pageable.getPageSize())
                                                                        .fetch();
        Long count = getCnt(keyword, startDate, endDate, nickName);
        return new PageImpl<>(list, pageable, count);

    }

    private Long getCnt(String keyword,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String nickName){
        return jpaQueryFactory.select(todo.count())
            .from(todo)
            .leftJoin(todo.managers, manager).on(manager.todo.id.eq(todo.id))
            .leftJoin(todo.comments, comment).on(comment.todo.id.eq(todo.id))
            .where(keywordEq(keyword), dateEq(startDate, endDate), nickNameEq(nickName))
            .fetchFirst();
    }

    private BooleanExpression keywordEq(String keyword) {
        if (keyword == null) {
            return null;
        }
        return todo.title.contains(keyword);
    }
    private BooleanExpression dateEq(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return todo.createdAt.between(startDate, endDate);
    }
    private BooleanExpression nickNameEq(String nickName) {
        if (nickName == null) {
            return null;
        }
        return manager.user.nickName.contains(nickName);
    }

}
