package com.swifty.bank.server.api.controller.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignWithFormResponse {
    @Schema(description = "회원가입/로그인에 성공하면 true, 실패시 false", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isSuccess;

    @Schema(description = "비밀번호 규칙 검증 실패 여부 반환. 성공시 true, 실패시 false", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isAvailablePassword;

    @Schema(description = "성공시 담길 AccessToken, RefreshToken", example = "{AccessToken}, {RefreshToken}", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> tokens;
}