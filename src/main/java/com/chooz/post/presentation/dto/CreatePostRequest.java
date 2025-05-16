package com.chooz.post.presentation.dto;

import com.chooz.post.domain.Scope;
import com.chooz.post.domain.VoteType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePostRequest(
        @NotNull
        String description,

        @Valid @NotNull
        List<PostImageRequestDto> images,

        @NotNull
        Scope scope,

        @NotNull
        VoteType voteType
) {
}
