package com.swifty.bank.server.exception.handler;

import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.exception.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthenticationExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        ResponseResult res = new ResponseResult(
                Result.FAIL,
                "[ERROR] 인증에 실패했습니다. 입력을 확인해 주세요",
                null
        );
        return ResponseEntity
                .badRequest()
                .body(res);
    }
}
