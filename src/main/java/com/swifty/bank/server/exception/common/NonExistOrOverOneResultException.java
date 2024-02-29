package com.swifty.bank.server.exception.common;

public class NonExistOrOverOneResultException extends IllegalArgumentException {
    private static final String defaultMsg = "[ERROR] 조회 결과가 1개가 아닙니다";
    public NonExistOrOverOneResultException( ) {
        super(defaultMsg);
    }
}
