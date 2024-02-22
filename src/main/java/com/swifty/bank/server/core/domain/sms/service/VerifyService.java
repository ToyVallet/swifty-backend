package com.swifty.bank.server.core.domain.sms.service;

public interface VerifyService {
    Boolean sendVerificationCode(String phoneNumber);

    Boolean checkVerificationCode(String phoneNumber, String verificationCode);
}
