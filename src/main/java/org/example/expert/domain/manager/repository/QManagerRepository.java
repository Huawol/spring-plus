package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QManagerRepository {

    Page<ManagerResponse> findManager(String nickname, Pageable pageable);
}
