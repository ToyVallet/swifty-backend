package com.swifty.bank.server.api.controller.dto.card.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCardRequest {
    @NotNull
    @Size(min = 4, max = 4)
    @Schema(description = "보안 키패드를 누른 순서", example = "[3, 7, 0, 4]",   requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Integer> pushedOrder;

    @NotNull
    @Schema(description = "UnitedAccount의 UUID",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID unitedAccountUuid;
}
