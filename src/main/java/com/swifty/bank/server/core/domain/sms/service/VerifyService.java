package com.swifty.bank.server.core.domain.sms.service;

import com.swifty.bank.server.core.domain.sms.constant.CheckVerificationStatus;
import com.swifty.bank.server.core.domain.sms.constant.SendVerificationStatus;

public interface VerifyService {
    SendVerificationStatus sendVerificationCode(String phoneNumber);

    CheckVerificationStatus checkVerificationCode(String phoneNumber, String verificationCode);
}
