package com.swifty.bank.server.core.common.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginWithFormRequest {
    @Size(max = 11, min = 3)
    @Pattern(regexp = "^\\d+$\n")
    private String phoneNumber;
    @NotNull
    @NotBlank
    private String deviceId;
}
