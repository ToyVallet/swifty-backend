package com.swifty.bank.server.api.controller.dto.customer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateSecureKeypadResponse {
    @Schema(name = "개인 식별 비밀번호 확인을 위한 키패드 이미지 제공",
            description = "순서가 섞인 키패드 이미지 리스트",
            example = "['<svg ... > </svg>\r\n', ... , '<svg ... > </svg>\r\n']")
    private List<String> keypad;
    
    @Schema(name = "키패드를 특정하기 위한 JWT")
    private String keypadToken;
}