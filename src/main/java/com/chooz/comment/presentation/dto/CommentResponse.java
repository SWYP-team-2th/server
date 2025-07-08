package com.chooz.comment.presentation.dto;

import com.chooz.common.dto.CursorBasePaginatedResponse;

public record CommentResponse(
        int commentCount,
        CursorBasePaginatedResponse<CommentDto> comments
){}
