package com.swifty.bank.server.api.controller.dto.customer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "비밀번호 확인 및 변경")
public class PasswordRequest {
    @Size(max = 6, min = 6)
    @Schema(example = "123456",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}