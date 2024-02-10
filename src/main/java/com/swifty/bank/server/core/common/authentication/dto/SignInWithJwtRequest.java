package com.swifty.bank.server.core.common.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "sign in with access token and device Id")
@Getter
public class SignInWithJwtRequest {
    @Schema(description = "device Id to compare previous logged in user and tried user")
    private String deviceId;

    public SignInWithJwtRequest(@JsonProperty("deviceId") String deviceId) {
        this.deviceId = deviceId;
    }
}
