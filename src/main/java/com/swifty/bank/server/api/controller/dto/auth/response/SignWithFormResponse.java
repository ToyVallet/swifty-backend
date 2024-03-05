package com.swifty.bank.server.api.controller.dto.auth.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignWithFormResponse {
    @Schema(description = "성공하면 true, 실패시 false", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isSuccess;
    @Schema(description = "성공시 담길 Access, Refresh token", example = "{Access Token}, {RefreshToken}", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> tokens;
}