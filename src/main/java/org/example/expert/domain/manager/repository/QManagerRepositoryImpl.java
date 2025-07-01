package org.example.expert.domain.manager.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QManagerRepositoryImpl implements QManagerRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ManagerResponse> findManager(String nickname, Pageable pageable) {
        List<ManagerResponse> content = queryFactory
                .select(Projections.constructor(
                        ManagerResponse.class,
                        manager.id,
                        Projections.constructor(
                                UserResponse.class,
                                manager.user.id,
                                manager.user.email,
                                manager.user.nickname
                        )
                ))
                .from(manager)
                .join(manager.user, user)
                .where(user.nickname.containsIgnoreCase(nickname))
                .orderBy(manager.todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(manager.count())
                .from(manager)
                .join(manager.user, user)
                .where(user.nickname.containsIgnoreCase(nickname))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
