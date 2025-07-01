package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSummaryResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface QTodoRepository {

    Optional<Todo> findByIdWithUser(Long id);
    Page<TodoResponse> findByTitle(String keyword, Pageable pageable);
    Page<TodoResponse> findAllByWeatherAndDateRange(String weather, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<TodoSummaryResponseDto> findTodoSummary(Pageable pageable);
}
