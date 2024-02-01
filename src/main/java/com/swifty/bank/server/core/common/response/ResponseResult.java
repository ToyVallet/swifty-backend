package com.swifty.bank.server.core.common.response;

import com.swifty.bank.server.core.common.constant.Result;
import lombok.Builder;
import lombok.Getter;

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
