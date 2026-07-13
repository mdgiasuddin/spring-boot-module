package com.example.module.springboottest.exception;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ExceptionResponse handleException(ResourceNotFoundException e) {
        return new ExceptionResponse("", e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(RulesViolationException.class)
    public ExceptionResponse handleException(RulesViolationException e) {
        return new ExceptionResponse(e.getCode(), e.getMessage());
    }

    @ResponseStatus(PRECONDITION_FAILED)
    @ExceptionHandler(SQLException.class)
    public ExceptionResponse handleException(SQLException e) {
        return new ExceptionResponse("SQL_VALIDATION_FAILED", e.getMessage());
    }


    @ResponseStatus(UNPROCESSABLE_CONTENT)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponse handleException(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return new ExceptionResponse(
                "VALIDATION_FAILED",
                String.join(". ", errors)
        );
    }
}
