package com.chooz.comment.presentation.dto;

import com.chooz.common.dto.CursorDto;

public record CommentResponse(
        Long id,
        Long userId,
        String nickname,
        String profileUrl,
        String content,
        int edited,
        int likeCount,
        boolean liked
) implements CursorDto  {

    @Override
    public long getId() {
        return 0;
    }
}
