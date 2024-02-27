package com.swifty.bank.server.exception.handler;

import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.exception.NotLoggedInCustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NotLoggedInExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handlerNotLoggedInException(NotLoggedInCustomerException e) {
        ResponseResult res = new ResponseResult(
                Result.FAIL,
                "[ERROR] 로그인 되지 않은 유저입니다",
                null
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(res);
    }
}
