package com.swifty.bank.server.api.controller.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Information for enroll or log in user")
public class SignWithFormRequest {
    @NotNull
    @Size(min = 6, max = 6)
    @Schema(description = "보안 키패드를 누른 순서", example = "[3, 3, 0, 1, 2, 9]")
    private List<Integer> pushedOrder;

    @NotNull
    @NotBlank
    @Schema(description = "device id")
    private String deviceId;
}