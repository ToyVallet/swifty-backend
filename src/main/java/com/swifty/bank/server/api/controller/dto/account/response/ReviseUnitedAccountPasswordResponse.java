package com.swifty.bank.server.api.controller.dto.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviseUnitedAccountPasswordResponse {
    @Schema(
            description = "성공시 true, 실패시 false",
            example = "true",
            requiredMode =  Schema.RequiredMode.REQUIRED
    )
    private boolean isSuccessful;
}
