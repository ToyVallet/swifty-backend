package com.swifty.bank.server.core.domain.product.repository;

import com.swifty.bank.server.core.domain.product.Product;
import com.swifty.bank.server.core.domain.product.repository.custom.ProductCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, ProductCustomRepository {
}
