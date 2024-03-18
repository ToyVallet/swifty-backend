package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.StealVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SendVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.StealVerificationCodeResponse;
import com.swifty.bank.server.api.service.SmsService;
import com.swifty.bank.server.core.common.redis.service.OtpRedisService;
import com.swifty.bank.server.core.domain.sms.service.VerifyService;
import com.swifty.bank.server.core.utils.RandomUtil;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {
    private final VerifyService verifyService;
    private final OtpRedisService otpRedisService;

    @Override
    public StealVerificationCodeResponse stealVerificationCode(
            StealVerificationCodeRequest stealVerificationCodeRequest) {
        String otp = RandomUtil.generateOtp(6);

        otpRedisService.setData(
                stealVerificationCodeRequest.getPhoneNumber(),
                otp,
                5L,
                TimeUnit.MINUTES
        );
        return StealVerificationCodeResponse.builder()
                .otp(otp)
                .build();
    }

    @Override
    public SendVerificationCodeResponse sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest) {
        boolean isSent = verifyService.sendVerificationCode(
                sendVerificationCodeRequest.getPhoneNumber());

        if (!isSent) {
            return SendVerificationCodeResponse.builder()
                    .isSuccess(false)
                    .build();
        }
        return SendVerificationCodeResponse.builder()
                .isSuccess(true)
                .build();
    }

    @Override
    public CheckVerificationCodeResponse checkVerificationCode(
            CheckVerificationCodeRequest checkVerificationCodeRequest) {
        boolean isValidVerificationCode = verifyService.checkVerificationCode(
                checkVerificationCodeRequest.getPhoneNumber(),
                checkVerificationCodeRequest.getVerificationCode());

        if (!isValidVerificationCode) {
            return CheckVerificationCodeResponse.builder()
                    .isSuccess(false)
                    .build();
        }
        return CheckVerificationCodeResponse.builder()
                .isSuccess(true)
                .build();
    }
}