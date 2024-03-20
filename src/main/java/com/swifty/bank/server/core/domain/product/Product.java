package com.swifty.bank.server.core.domain.product;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.common.constant.ProductType;
import com.swifty.bank.server.core.domain.BaseEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_product")
public class Product extends BaseEntity {
    @Id
    private String abbreviation;
    private String name;
    private ProductType productType;
    @ElementCollection
    private List<Currency> currencies;
}
