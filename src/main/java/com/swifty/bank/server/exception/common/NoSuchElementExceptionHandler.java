package com.swifty.bank.server.exception.common;

import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NoSuchElementExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException noSuchElementException) {
        ResponseResult res = new ResponseResult(Result.FAIL,
                "[ERROR] 조회 결과가 존재하지 않습니다. 백엔드 측에 문의하거나 입력을 확인하세요",
                null);

        return ResponseEntity
                .badRequest()
                .body(res);
    }
}
