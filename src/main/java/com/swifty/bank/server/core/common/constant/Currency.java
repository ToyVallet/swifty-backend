package com.swifty.bank.server.core.common.constant;

public enum Currency {
    KRW,
    JPY,
    CNY,
    USD,
    CAD,
    AUD,
    EUR,
    GBP,
    RUB,
    INR,
    PHP,
    THB,
    TRY,
    SAR,
    VND,
    MXN;

    public boolean sameCurrency(Currency c) {
        if (this.toString().equals(c.toString()))   return true;
        return false;
    }
}
