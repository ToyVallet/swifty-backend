package com.swifty.bank.server.api.controller.dto.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "UnitedAccount 비밀번호 수정 요청 폼")
@NoArgsConstructor
public class ReviseUnitedAccountPasswordRequest {
    @NotNull
    @Schema(description = "UnitedAccount의 UUID")
    private UUID accountUuid;

    @NotNull
    @Size(min = 4, max = 4)
    @Schema(description = "보안 키패드를 누른 순서", example = "[3, 7, 0, 4]")
    private List<Integer> pushedOrder;
}