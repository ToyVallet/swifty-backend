package com.swifty.bank.server.api.controller.dto.account.request;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.common.constant.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRegisterRequest {
    private Product product;
    @NotNull
    @Size(min = 4, max = 4)
    @Schema(description = "보안 키패드를 누른 순서", example = "[3, 7, 0, 4]")
    private List<Integer> pushedOrder;
    private List<Currency> currencies;
    private Currency defaultCurrency;
    private int registerLimit;
}