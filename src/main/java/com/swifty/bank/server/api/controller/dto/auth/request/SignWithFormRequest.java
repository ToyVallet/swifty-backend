package com.swifty.bank.server.api.controller.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Information for enroll or log in user")
public class SignWithFormRequest {
    @NotNull
    @NotBlank
    @Size(min = 6, max = 6)
    private String password;

    @NotNull
    @NotBlank
    private String deviceId;
}