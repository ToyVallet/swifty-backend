package com.swifty.bank.server.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
    @Schema(description = "전달하고 싶은 메세지", example = "xx의 이유로 요청이 실패했습니다, xx의 이유로 에러가 발생했습니다 등")
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}