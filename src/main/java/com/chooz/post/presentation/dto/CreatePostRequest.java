package com.chooz.post.presentation.dto;

import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Scope;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreatePostRequest(

        @NotBlank
        String title,

        @NotNull
        String description,

        @Valid
        @NotNull
        List<PollChoiceRequestDto> pollChoices,

        @Valid
        @NotNull
        PollOptionDto pollOption,

        @Valid
        @NotNull
        CloseOptionDto closeOption
) {

        public record PollOptionDto(
                @NotNull
                Scope scope,

                @NotNull
                PollType pollType,

                @NotNull
                CommentActive commentActive
        ) { }

        public record CloseOptionDto(
                @NotNull
                CloseType closeType,

                Integer maxVoterCount,

                LocalDateTime closedAt
        ) { }
}
