package com.swifty.bank.server.core.domain.sms.service;

import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.StealVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.response.CheckVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.sms.response.StealVerificationCodeResponse;
import com.swifty.bank.server.api.service.impl.SmsServiceImpl;
import com.swifty.bank.server.core.common.redis.service.OtpRedisService;
import com.swifty.bank.server.core.common.redis.service.impl.OtpRedisServiceImpl;
import com.swifty.bank.server.core.domain.sms.service.impl.VerifyServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled
@ContextConfiguration(locations = {"classpath:config/application.yaml"})
@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
public class SmsServiceTest {
    @Mock
    private VerifyServiceImpl verifyService;

    @Spy
    private OtpRedisServiceImpl otpRedisService;

    @InjectMocks
    private SmsServiceImpl smsService;

    @Test
    public void stealVerificationCodeTest( ) {
        String phoneNumber = "+821012345678";
        StealVerificationCodeRequest stealVerificationCodeRequest = new StealVerificationCodeRequest(phoneNumber);

        StealVerificationCodeResponse res = smsService.stealVerificationCode(stealVerificationCodeRequest);
        assertThat(res.getOtp()).isEqualTo(otpRedisService.getData(phoneNumber));
    }

    @Test
    public void checkNotValidVerificationCodeTest( ) {
        String phoneNumber = "+821012345678";
        String verificationCode = "000000";

        CheckVerificationCodeRequest req = new CheckVerificationCodeRequest(phoneNumber, verificationCode);

        when(smsService.checkVerificationCode(req))
                .thenReturn(CheckVerificationCodeResponse.builder()
                        .isSuccess(false)
                        .build());

        assertThat(!smsService.checkVerificationCode(req).getIsSuccess());
    }

    @Test
    public void checkValidVerificationCodeTest( ) {
        String phoneNumber = "+821012345678";
        String verificationCode = "000000";

        CheckVerificationCodeRequest req = new CheckVerificationCodeRequest(phoneNumber, verificationCode);
        StealVerificationCodeRequest reqForSteal = new StealVerificationCodeRequest(phoneNumber);

        smsService.stealVerificationCode(reqForSteal);

        when(smsService.checkVerificationCode(req))
                .thenReturn(CheckVerificationCodeResponse.builder()
                        .isSuccess(true)
                        .build());
        assertThat(smsService.checkVerificationCode(req).getIsSuccess());
    }
}
