package com.strutynskyi.api.exceptions;

public class NoMoviesByDirectorFoundException extends RuntimeException {
    private final static String MESSAGE = "There is no director: ";
    public NoMoviesByDirectorFoundException(String firstName, String lastName) {
        super(MESSAGE.concat(firstName).concat(" ").concat(lastName));
    }
}
