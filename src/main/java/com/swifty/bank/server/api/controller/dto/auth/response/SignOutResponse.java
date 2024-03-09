package com.swifty.bank.server.api.controller.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SignOutResponse {
    @Schema(description = "성공하면 true, 실패시 false",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean wasSignedOut;
}
