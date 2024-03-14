package com.swifty.bank.server.exception.common;

import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
@Slf4j
@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.info("exceptionHandler={}",e.getMessage());
        
        ResponseResult res = new ResponseResult(
                Result.FAIL,
                e.getMessage(),
//                "[ERROR] 알 수 없는 예외가 발생했습니다. 백엔드 단에 문의해 주세요",
                null
        );
        return ResponseEntity
                .internalServerError()
                .body(res);
    }
}
