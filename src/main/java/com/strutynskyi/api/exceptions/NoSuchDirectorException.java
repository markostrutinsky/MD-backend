package com.strutynskyi.api.exceptions;

public class NoSuchDirectorException extends RuntimeException {
    private static final String MESSAGE = "No such director";
    public NoSuchDirectorException() {
        super(MESSAGE);
    }
}
