package com.swifty.bank.server.api.controller.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReissueResponse {
    @Schema(description = "성공시 true, 실패시 false",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isSuccess;

    @Schema(description = "성공시 AccessToken, RefreshToken 반환",
            example = "[{AccessToken}, {RefreshToken}]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> tokens;
}
