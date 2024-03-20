package com.swifty.bank.server.core.domain.product.repository.custom;

import com.swifty.bank.server.core.common.constant.ProductType;
import com.swifty.bank.server.core.domain.product.Product;

import java.util.List;

public interface ProductCustomRepository {
    List<Product> getProductsByType(ProductType productType);
    Product getProductByAbbr(String abbr);
}
