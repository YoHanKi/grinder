package com.grinder.exception;

import lombok.NoArgsConstructor;

public class ContentTypeException extends RuntimeException {

    public ContentTypeException(String message) {
        super(message);
    }
}
