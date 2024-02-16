package com.swifty.bank.server.core.domain.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "DTO to get a password from user")
public class PasswordRequest {
    @Schema(description = "plain string for password")
    private String passwd;
}