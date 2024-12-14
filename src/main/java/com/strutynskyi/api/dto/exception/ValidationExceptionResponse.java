package com.strutynskyi.api.dto.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

@Data
public class ValidationExceptionResponse {
    private HttpStatus status;
    private List<String> messages;
}

