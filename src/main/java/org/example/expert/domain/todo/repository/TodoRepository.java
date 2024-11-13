package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TodoRepository extends JpaRepository<Todo, Long> , CustomTodoRepository{

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("select t from Todo t inner JOIN fetch t.user where t.weather = :weather order by t.modifiedAt desc")
    Page<Todo> findByWeather(String weather, Pageable pageable);

    @Query("select t from Todo t inner JOIN fetch t.user where t.modifiedAt between :startDate and :endDate order by t.modifiedAt desc")
    Page<Todo> findByModifyAt(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select t from Todo t inner JOIN fetch t.user where t.weather = :weather and t.modifiedAt between :startDate and :endDate order by t.modifiedAt desc")
    Page<Todo> findByWeatherAndModifyAt(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
