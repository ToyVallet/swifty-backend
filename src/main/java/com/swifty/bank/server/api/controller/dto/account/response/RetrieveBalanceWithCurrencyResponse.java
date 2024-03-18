package com.swifty.bank.server.api.controller.dto.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RetrieveBalanceWithCurrencyResponse {
    @Schema(description = "실패시 false, 성공시 true", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isSuccessful;

    @Schema(description = "환전 결과 잔액", example = "KRW : 4000")
    Map<String, Double> res;
}
