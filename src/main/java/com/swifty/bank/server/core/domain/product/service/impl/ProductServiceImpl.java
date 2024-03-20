package com.swifty.bank.server.core.domain.product.service.impl;

import com.swifty.bank.server.core.common.constant.ProductType;
import com.swifty.bank.server.core.domain.product.Product;
import com.swifty.bank.server.core.domain.product.repository.ProductRepository;
import com.swifty.bank.server.core.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<Product> findProductByProductType(ProductType productType) {
        return productRepository.getProductsByType(productType);
    }

    @Override
    public Product findProductByAbbr(String abbr) {
        return productRepository.getProductByAbbr(abbr);
    }
}
