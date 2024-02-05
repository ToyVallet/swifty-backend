package com.swifty.bank.server.core.domain.sms.service.impl;

import com.swifty.bank.server.core.domain.sms.constant.CheckVerificationStatus;
import com.swifty.bank.server.core.domain.sms.constant.SendVerificationStatus;
import com.swifty.bank.server.core.domain.sms.service.VerifyService;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TwilioVerifyService implements VerifyService {
    @Value("${TWILIO_ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @Value("${TWILIO_VERIFY_SID}")
    private String VERIFY_SID;

    @PostConstruct
    private void setup() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }


    @Override
    public SendVerificationStatus sendVerificationCode(String phoneNumber) {
        try {
            Verification verification = Verification.creator(
                            VERIFY_SID,
                            phoneNumber,
                            "sms")
                    .create();
            log.info(verification.toString());
            return SendVerificationStatus.findByTwilioStatus(verification.getStatus());
        } catch (Exception e) {
            log.info(e.getMessage());
            return SendVerificationStatus.CANCELED;
        }
    }

    @Override
    public CheckVerificationStatus checkVerificationCode(String phoneNumber, String verificationCode) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(
                            VERIFY_SID)
                    .setTo(phoneNumber)
                    .setCode(verificationCode)
                    .create();
            log.info(verificationCheck.toString());
            return CheckVerificationStatus.findByTwilioStatus(verificationCheck.getStatus());
        } catch (Exception e) {
            log.info("verification failed");
            return CheckVerificationStatus.CANCELED;
        }
    }
}