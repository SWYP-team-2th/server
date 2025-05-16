package com.chooz.post.application;

import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.InternalServerException;

public class PostImageNameGenerator {

    private int index = 0;
    private final String[] alphabets = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    public String generate() {
        if (index >= alphabets.length) {
            throw new InternalServerException(ErrorCode.POST_IMAGE_NAME_GENERATOR_INDEX_OUT_OF_BOUND);
        }
        return "뽀또" + alphabets[index++];
    }
}
