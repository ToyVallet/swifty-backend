package com.swifty.bank.server.api.controller.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckLoginAvailabilityResponse {
    @Schema(description = "회원가입/로그인 가능 여부. 신규 회원인 경우 true. 기존 회원이면서 폼과 정보가 일치하는 경우는 true. 기존 회원이면서 폼과 정보가 불일치하는 경우 false.", example = "true", requiredMode = RequiredMode.REQUIRED)
    private Boolean isAvailable;

    @Schema(description = "비밀번호 설정까지만 사용할 임시 JWT", example = "{발급된 JWT 문자열}", requiredMode = RequiredMode.REQUIRED)
    private String temporaryToken;
}
