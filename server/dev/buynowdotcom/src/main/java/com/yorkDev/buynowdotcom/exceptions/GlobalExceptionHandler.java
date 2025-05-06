package com.yorkDev.buynowdotcom.exceptions;

import com.yorkDev.buynowdotcom.response.ApiResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import static  org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiResponse> handleAlreadyExists(EntityExistsException e) {
        return ResponseEntity
                .status(CONFLICT)
                .body(new ApiResponse("Conflict: ", e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFoundExists(EntityNotFoundException e) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ApiResponse("Not Found: ", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralExceptions(Exception e) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Server Error: ", e.getMessage()));
    }
}
