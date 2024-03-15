package com.swifty.bank.server.api.controller.dto.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountRegisterResponse {
    @Schema(description = "성공하면 true, 실패시 false",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isSuccessful;
}
