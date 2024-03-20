package com.swifty.bank.server.core.domain.product;

import com.swifty.bank.server.core.common.constant.ProductType;
import com.swifty.bank.server.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tb_product")
public class Product extends BaseEntity {
    @Id
    private String abbreviation;
    private String name;
    private ProductType productType;
}
