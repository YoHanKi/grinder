package com.grinder.exception;

import lombok.NoArgsConstructor;

public class RecentAddedTagException extends RuntimeException {

    public RecentAddedTagException(String message) {
        super(message);
    }
}
