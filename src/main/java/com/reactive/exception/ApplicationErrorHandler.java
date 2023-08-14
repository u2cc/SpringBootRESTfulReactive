package com.reactive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author u2cc
 */

@ControllerAdvice
public class ApplicationErrorHandler {
    @ExceptionHandler(DiecastCarAlreadyExistsException.class)
    public ResponseEntity exception(DiecastCarAlreadyExistsException exception) {
        //return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diecast car not found."));
        return new ResponseEntity<>("Diecast car or given ID already exists.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DiecastCarNotFoundException.class)
    public ResponseEntity exception(DiecastCarNotFoundException exception) {
        //return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diecast car not found."));
        return new ResponseEntity<>("Diecast car or given ID not found for update.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalRequestException.class)
    public ResponseEntity exception(IllegalRequestException exception) {
        //return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diecast car not found."));
        return new ResponseEntity<>("Dont provide id when adding a new car", HttpStatus.BAD_REQUEST);
    }
}
