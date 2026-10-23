package com.alexa.healthassistant.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice     
public class GlobalExceptionHandler{

    //handles validation errors from @Valid(missing, blank fields)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex){
        Map<String, String> errors= new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e-> errors.put(e.getField(), e.getDefaultMessage()));
      return ResponseEntity.badRequest().body(errors);
          
    }

    //our rule validation, like from after to
  @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex){
        Map<String, String> errors= new HashMap<>();
        return ResponseEntity.badRequest().body(Map.of("Error!", ex.getMessage()));
    }
  //Bad Date format in the URL , like from=abc
   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
     public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
       
       return ResponseEntity.badRequest().body(Map.of("Errror , Invalid Valid for "+ex.getName(), ex.getMessage()));
     }

     //Unreadable Json ,or a wrong ENUM value

     @ExceptionHandler(HttpMessageNotReadableException.class)
     public ResponseEntity<Map<String,String>> handleUnreadable(HttpMessageNotReadableException ex){
       return ResponseEntity.badRequest().body(Map.of("Error!", "Request Body is missing or Unreadable"));

     }

    // Anything unexpected: hide the details from the caller
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<Map<String, String>> handleAny(Exception ex) {
    //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body(Map.of("error", "Something went wrong"));
    // }
}


