package com.swifty.bank.server.api.controller.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Information for enroll or log in user")
public class SignRequest {
    private String password;
    private String deviceId;
}