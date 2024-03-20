package com.swifty.bank.server.api.controller.dto.account.response;

import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.dto.UnitedAccountDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ListUnitedAccountWithCustomerResponse {
    @Schema(description = "성공시 true, 실패시 false", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isSuccessful;

    @Schema(
            description = "모든 통장의 잔액 및 환 정보",
            example =
                    """
                    {
                        {
                            "uuid" : "{account uuid}",
                            "accountNumber" : "010101-101010",
                            ...
                            "subAccount" : {
                                "uuid" : "{subaccount uuid}",
                                "currency" : "KRW",
                                "balance" : "3000"
                            }
                        },
                        ...
                    }        
                    """
    )
    private List<UnitedAccountDto> unitedAccounts;
}
