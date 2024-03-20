package com.swifty.bank.server.core.common.constant;

public enum ProductType {
    ACCOUNT,
    CARD;

    public boolean sameProductType(ProductType productType) {
        return this.toString().equals(productType.toString());
    }
}
