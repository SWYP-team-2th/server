package com.chooz.comment.presentation.dto;

import com.chooz.common.dto.CursorBasePaginatedResponse;

public record CommentResponse(
        Long commentCount,
        CursorBasePaginatedResponse<CommentDto> commentDto
){}
