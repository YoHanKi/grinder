package com.grinder.exception;

import lombok.NoArgsConstructor;

public class NoMoreContentException extends RuntimeException {

    public NoMoreContentException(String message) {
        super(message);
    }
}