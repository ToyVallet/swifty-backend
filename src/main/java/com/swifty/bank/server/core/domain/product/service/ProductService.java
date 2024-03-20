package com.swifty.bank.server.core.domain.product.service;

import com.swifty.bank.server.core.common.constant.ProductType;
import com.swifty.bank.server.core.domain.product.Product;

import java.util.List;

public interface ProductService {

    List<Product> findProductByProductType(ProductType productType);

    Product findProductByAbbr(String abbr);
}
