package com.strutynskyi.api.exceptions;

public class MovieAlreadyExistsException extends RuntimeException {
    private final static String MESSAGE = "Movie already exists";
    public MovieAlreadyExistsException() {
        super(MESSAGE);
    }
}
