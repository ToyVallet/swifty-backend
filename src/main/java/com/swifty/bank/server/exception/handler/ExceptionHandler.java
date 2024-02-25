package com.swifty.bank.server.exception.handler;

import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<?> handleException(Exception e) {
        ResponseResult res = new ResponseResult(
                Result.FAIL,
                "[ERROR] 알 수 없는 예외가 발생했습니다. 백엔드 단에 문의해 주세요",
                null
        );
        return ResponseEntity
                .internalServerError()
                .body(res);
    }
}
