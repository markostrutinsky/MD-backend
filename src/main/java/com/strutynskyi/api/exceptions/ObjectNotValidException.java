package com.strutynskyi.api.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ObjectNotValidException extends RuntimeException {
    private List<String> errors;

    public ObjectNotValidException(List<String> errors) {
        super(String.join(" ", errors));
        this.errors = errors;
    }
}

