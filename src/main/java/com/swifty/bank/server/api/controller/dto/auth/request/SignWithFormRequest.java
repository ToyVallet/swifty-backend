package com.swifty.bank.server.api.controller.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "최종 회원가입/로그인 폼")
public class SignWithFormRequest {
    @NotNull
    @Size(min = 6, max = 6)
    @Schema(description = "보안 키패드를 누른 순서", example = "[3, 3, 0, 1, 2, 9]")
    private List<Integer> pushedOrder;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 40)
    @Schema(description = "디바이스 아이디",
            example = "12345678-1234-5678-1234-567812345678")
    private String deviceId;
}