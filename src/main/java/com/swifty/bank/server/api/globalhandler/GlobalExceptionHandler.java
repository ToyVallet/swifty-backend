package com.swifty.bank.server.api.globalhandler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGlobalExceptions(
      Exception ex) {
      log.info("{}",ex.getMessage());
      return new ResponseEntity<>(HttpStatus.OK);
  }
}