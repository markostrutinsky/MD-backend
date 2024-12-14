package com.strutynskyi.api.handler;

import com.strutynskyi.api.dto.exception.ExceptionResponse;
import com.strutynskyi.api.dto.exception.ValidationExceptionResponse;
import com.strutynskyi.api.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger("project");

    private static final Map<Class<? extends Exception>, HttpStatus> exceptionStatusMap = Map.of(
            DirectorAlreadyExistsException.class, HttpStatus.CONFLICT,
            MovieAlreadyExistsException.class, HttpStatus.CONFLICT,
            NoMoviesByDirectorFoundException.class, HttpStatus.NOT_FOUND,
            NoSuchDirectorException.class, HttpStatus.NOT_FOUND,
            NoSuchMovieException.class, HttpStatus.NOT_FOUND,
            ObjectNotValidException.class, HttpStatus.BAD_REQUEST,
            IllegalArgumentException.class, HttpStatus.BAD_REQUEST
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        logger.error("GlobalExceptionHandler:handleException() An exception occurred: {}", ex.getMessage());

        ExceptionResponse errorObject = new ExceptionResponse();
        errorObject.setMessage(ex.getMessage());

        HttpStatus status = exceptionStatusMap.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        errorObject.setStatus(status);

        return new ResponseEntity<>(errorObject, status);
    }

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<ValidationExceptionResponse> handleObjectNotValidException(ObjectNotValidException ex) {
        logger.error("GlobalExceptionHandler:handleObjectNotValidException() An exception occurred: {}", ex.getMessage());

        ValidationExceptionResponse errorObject = new ValidationExceptionResponse();
        errorObject.setMessages(ex.getErrors());

        HttpStatus status = exceptionStatusMap.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        errorObject.setStatus(status);

        return new ResponseEntity<>(errorObject, status);
    }
}