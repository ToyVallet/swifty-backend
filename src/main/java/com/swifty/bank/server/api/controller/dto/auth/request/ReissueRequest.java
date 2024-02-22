package com.swifty.bank.server.api.controller.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "form with refresh token when reissue tokens")
@Data
public class ReissueRequest {
    @Schema(description = "refresh token content without Bearer, plain text", required = true)
    private String refreshToken;

    public ReissueRequest(@JsonProperty("refreshToken") String refreshToken) {
        this.refreshToken = refreshToken;
    }
}