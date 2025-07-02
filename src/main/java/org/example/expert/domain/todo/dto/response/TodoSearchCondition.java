package org.example.expert.domain.todo.dto.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TodoSearchCondition {

    private String title;
    private String nickname;
    private LocalDate startDate;
    private LocalDate endDate;
}
