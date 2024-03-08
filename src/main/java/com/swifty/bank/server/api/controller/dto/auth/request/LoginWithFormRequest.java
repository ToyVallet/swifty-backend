package com.swifty.bank.server.api.controller.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "request with phone number and device id to identification")
public class LoginWithFormRequest {
    @NotNull
    @Size(max = 14, min = 3)
    @Pattern(regexp = "^\\+82\\d+$\n")  // start with +82 and only digits
    @Schema(description = "start with +82 and only digits 0-9 without dash", example = "+8201012345678",
            required = true)
    private String phoneNumber;
    @NotNull
    @NotBlank
    @Schema(description = "plain string for device id", example = "{decided by frontend}")
    private String deviceId;
}