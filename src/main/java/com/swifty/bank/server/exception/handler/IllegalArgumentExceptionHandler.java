package com.swifty.bank.server.exception.handler;

import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class IllegalArgumentExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handlerIllegalArgumentException(IllegalArgumentException e) {
        ResponseResult res = new ResponseResult(
                Result.FAIL,
                "[ERROR] 유효하지 않은 입력입니다. 입력을 검토하거나 백엔드 팀에 문의하세요",
                null
        );
        return ResponseEntity
                .accepted()
                .body(res);
    }
}
