package com.swifty.bank.server.api.service.dto;

import lombok.Builder;
import lombok.Getter;

/*
 * 모든 Service
 * */
@Getter
public class ResponseResult<T> {
    private Result result;
    private String message;
    private T data;

    @Builder
    public ResponseResult(Result result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }
}
