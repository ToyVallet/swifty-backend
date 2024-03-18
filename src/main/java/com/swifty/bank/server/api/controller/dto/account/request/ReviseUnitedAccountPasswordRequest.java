package com.swifty.bank.server.api.controller.dto.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "UnitedAccount 비밀번호 수정 요청 폼")
@NoArgsConstructor
@RequiredArgsConstructor
public class ReviseUnitedAccountPasswordRequest {
    @NotNull
    @Schema(description = "UnitedAccount의 UUID",
            requiredMode = RequiredMode.REQUIRED)
    private UUID accountUuid;

    @NotNull
    @Size(min = 4, max = 4)
    @Schema(description = "보안 키패드를 누른 순서",
            example = "[3, 7, 0, 4]",
            requiredMode = RequiredMode.REQUIRED)
    private List<Integer> pushedOrder;
}