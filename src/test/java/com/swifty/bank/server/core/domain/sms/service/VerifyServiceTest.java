package com.swifty.bank.server.core.domain.sms.service;

import com.swifty.bank.server.core.common.redis.service.impl.OtpRedisServiceImpl;
import com.swifty.bank.server.core.domain.sms.service.impl.TwilioMessageService;
import com.swifty.bank.server.core.domain.sms.service.impl.VerifyServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@Disabled
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(locations = {"classpath:config/application.yaml"})
@WebAppConfiguration
public class VerifyServiceTest {

    @Spy
    private OtpRedisServiceImpl redisService;

    @InjectMocks
    private VerifyServiceImpl verifyService;

    private final Long verificationTimeout = 5L;

    @Test
    public void checkVerificationWithNonExistOtpTest( ) {
        String phoneNumber = "+821012345678";
        String expectedOtp = "123456";

        assertThat(!verifyService.sendVerificationCode(expectedOtp));
    }

    @Test
    public void isVerifiedWithNonExistPhoneNumberTest( ) {
        String phoneNumber = "+821012345678";

        assertThat(!verifyService.isVerified(phoneNumber));
    }

    @Test
    public void checkVerificationWithNotMatchOtpTest( ) {
        String phoneNumber = "+821012345678";
        String stored = "000000";
        String wrong = "123456";

        redisService.setData(
                phoneNumber,
                stored,
                verificationTimeout,
                TimeUnit.MINUTES
        );

        assertThat(!verifyService.checkVerificationCode(phoneNumber, wrong));
    }

    @Test
    public void checkVerificationWithMatchOtpTest( ) {
        String phoneNumber = "+821012345678";
        String stored = "000000";

        redisService.setData(
                phoneNumber,
                stored,
                verificationTimeout,
                TimeUnit.MINUTES
        );

        assertThat(verifyService.checkVerificationCode(phoneNumber, stored));
    }

    @Test
    public void successfulIsVerified( ) {
        String phoneNumber = "+821012345678";
        String stored = "000000";

        redisService.setData(
                phoneNumber,
                stored,
                verificationTimeout,
                TimeUnit.MINUTES
        );

        verifyService.checkVerificationCode(phoneNumber, stored);
        assertThat(verifyService.isVerified(phoneNumber));
    }
}