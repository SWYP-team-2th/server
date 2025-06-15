package com.chooz.common.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;

import java.util.List;

@Slf4j
public record CursorBasePaginatedResponse<T>(
        Long nextCursor,
        boolean hasNext,
        List<T> data
) {

    public static <T extends CursorDto> CursorBasePaginatedResponse<T> of(Slice<T> slice) {
        return new CursorBasePaginatedResponse<>(
                slice.getContent().isEmpty() ? null : slice.getContent().getLast().getId(),
                slice.hasNext(),
                slice.getContent()
        );
    }
}
