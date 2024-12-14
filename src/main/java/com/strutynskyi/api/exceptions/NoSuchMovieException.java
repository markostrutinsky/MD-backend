package com.strutynskyi.api.exceptions;

public class NoSuchMovieException extends RuntimeException {
    private final static String MESSAGE = "There is no such movie";
    public NoSuchMovieException() {
        super(MESSAGE);
    }
}
