package com.swifty.bank.server.api.controller.dto.account.response;

import com.swifty.bank.server.core.domain.product.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public class ListOfAccountProductResponse {
    @Schema(description = "계좌에 해당하는 상품들을 반환한다", requiredMode = Schema.RequiredMode.REQUIRED, example =
            """
            "products": {
                "product": {"abbreviation" : "SWIFTY", "name" : "Swifty", "productType" : "ACCOUNT" },
                ...
            }
            """
    )
    private List<Product> products;
}
