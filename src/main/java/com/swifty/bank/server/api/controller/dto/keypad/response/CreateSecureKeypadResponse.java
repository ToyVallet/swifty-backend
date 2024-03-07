package com.swifty.bank.server.api.controller.dto.keypad.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateSecureKeypadResponse {
    @Schema(description = "순서가 섞인 키패드 이미지 리스트")
    private String[] keypad;
}