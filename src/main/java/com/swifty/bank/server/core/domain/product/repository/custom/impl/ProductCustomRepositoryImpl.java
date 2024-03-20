package com.swifty.bank.server.core.domain.product.repository.custom.impl;

import com.swifty.bank.server.core.common.constant.ProductType;
import com.swifty.bank.server.core.domain.product.Product;
import com.swifty.bank.server.core.domain.product.repository.custom.ProductCustomRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {
    private final EntityManager em;
    private final boolean isDeleted = false;

    @Override
    public List<Product> getProductsByType(ProductType productType) {
        return em.createQuery("SELECT P FROM Product P WHERE P.isDeleted = :isDeleted AND P.productType = :productType",
                Product.class)
                .setParameter("isDeleted", isDeleted)
                .setParameter("productType", productType)
                .getResultList();
    }

    @Override
    public Product getProductByAbbr(String abbr) {
        return em.createQuery("SELECT P FROM Product P WHERE P.isDeleted = :isDeleted AND P.abbreviation = :abbr",
                Product.class)
                .setParameter("isDeleted", isDeleted)
                .setParameter("abbr", abbr)
                .getSingleResult();
    }
}
