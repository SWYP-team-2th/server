package com.chooz.post.presentation.dto;

import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Scope;
import jakarta.validation.constraints.NotNull;

public record PollOptionDto(
        @NotNull
        Scope scope,

        @NotNull
        PollType pollType,

        @NotNull
        CommentActive commentActive
) { }

