package com.y.fish.base.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by myliang on 11/7/17.
 */
@ControllerAdvice
@RestController
public class ApplicationErrorHandler {

    Logger logger = LoggerFactory.getLogger("ApplicationExceptionHandler");

    @ExceptionHandler(Exception.class)
    public Object defaultErrorHandler(HttpServletRequest req, Exception e) {
        logger.error("Exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("X-Message", e.getMessage()).build();
    }

    @ExceptionHandler(value=MethodArgumentNotValidException.class)
    public ResponseEntity validErrorHandler(HttpServletRequest req, MethodArgumentNotValidException e) {
        logger.error("MethodArgumentNotValidException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("X-Message", e.getMessage()).build();
    }

}
