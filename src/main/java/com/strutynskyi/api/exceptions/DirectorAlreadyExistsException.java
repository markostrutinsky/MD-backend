package com.strutynskyi.api.exceptions;

public class DirectorAlreadyExistsException extends RuntimeException {
    private final static String MESSAGE = "Director already exists";
    public DirectorAlreadyExistsException() {
        super(MESSAGE );
    }
}
