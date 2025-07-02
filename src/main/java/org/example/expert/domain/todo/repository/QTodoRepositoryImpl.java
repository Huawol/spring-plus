package org.example.expert.domain.todo.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSummaryResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QTodoRepositoryImpl implements QTodoRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(todo)
                        .leftJoin(todo.user, user).fetchJoin()
                        .where(todo.id.eq(id))
                        .fetchOne());
    }

    @Override
    public Page<TodoResponse> findByTitle(String keyword, Pageable pageable) {

        List<Todo> content = queryFactory
                .selectFrom(todo)
                .where(todo.title.containsIgnoreCase(keyword))
                .orderBy(todo.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(todo.title.containsIgnoreCase(keyword))
                .fetchOne();

        List<TodoResponse> dtoList = content.stream()
                .map(t -> new TodoResponse(
                        t.getId(),
                        t.getTitle(),
                        t.getContents(),
                        t.getWeather(),
                        new UserResponse(
                                t.getUser().getId(),
                                t.getUser().getEmail(),
                                t.getUser().getNickname()
                        ),
                        t.getCreatedAt(),
                        t.getModifiedAt()
                )).toList();

        return new PageImpl<>(dtoList, pageable, total != null ? total : 0);
    }

    @Override
    public Page<TodoResponse> findAllByWeatherAndDateRange(String weather, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (weather != null) {
            builder.and(todo.weather.eq(weather));
        }
        if (startDate != null) {
            builder.and(todo.modifiedAt.goe(startDate.atStartOfDay()));
        }
        if (endDate != null) {
            builder.and(todo.modifiedAt.loe(endDate.atStartOfDay()));
        }
        List<TodoResponse> content = queryFactory
                .select(Projections.constructor(
                        TodoResponse.class,
                        todo.id,
                        todo.title,
                        todo.contents,
                        todo.weather,
                        Projections.constructor(UserResponse.class,
                                todo.user.id,
                                todo.user.email,
                                todo.user.nickname
                        ),
                        todo.createdAt,
                        todo.modifiedAt
                ))
                .from(todo)
                .join(todo.user)
                .where(builder)
                .orderBy(todo.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 전체 개수 쿼리 (count)
        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(builder)
                .fetchOne();

        // 3. Page로 변환
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<TodoSummaryResponseDto> findTodoSummary(Pageable pageable) {

        List<TodoSummaryResponseDto> content = queryFactory
                .select(Projections.constructor(
                        TodoSummaryResponseDto.class,
                        todo.id,
                        todo.title,
                        manager.countDistinct(),
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.todo.id.eq(todo.id))
                ))
                .from(todo)
                .leftJoin(manager).on(manager.todo.id.eq(todo.id))
                .groupBy(todo.id, todo.title)
                .orderBy(todo.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }


}
