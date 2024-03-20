package com.swifty.bank.server.api.controller.dto.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "계좌 비밀번호 입력을 위한 키패드 이미지 제공")
public class CreateSecureKeypadResponse {
    @Schema(description = "순서가 섞인 키패드 이미지 리스트",
            example = "['<svg ... > </svg>\r\n', ... , '<svg ... > </svg>\r\n']")
    private List<String> keypad;

    @Schema(description = "키패드를 특정하기 위한 JWT")
    private String keypadToken;
}