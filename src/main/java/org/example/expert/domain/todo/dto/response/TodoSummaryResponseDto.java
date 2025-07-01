package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoSummaryResponseDto {

    private Long todoId;
    private String title;
    private Long managerCount;
    private Long commentCount;

    public TodoSummaryResponseDto(Long todoId, String title, Long managerCount, Long commentCount) {
        this.todoId = todoId;
        this.title = title;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }
}
