package com.swifty.bank.server.api.controller.dto.customer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 확인 및 변경")
public class PasswordRequest {
    @NotNull
    @Size(min = 4, max = 4)
    @Schema(description = "보안 키패드를 누른 순서",
            example = "[3, 7, 0, 4]",
            requiredMode = RequiredMode.REQUIRED)
    private List<Integer> pushedOrder;
}