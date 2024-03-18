package com.swifty.bank.server.api.controller.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutResponse {
    @Schema(description = "로그아웃에 성공한 경우에는 true, 실패시 false를 반환합니다", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Boolean isSuccess;
}
