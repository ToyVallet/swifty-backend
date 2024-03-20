package com.swifty.bank.server.core.common.constant;

public enum Product {
    SEED,
    MOUNTAIN,
    WIRACLE,
    HELLO,
    YOURIAL;

    public boolean sameProduct(Product p) {
        if (this.toString().equals(p.toString()))   return true;
        return false;
    }
}
