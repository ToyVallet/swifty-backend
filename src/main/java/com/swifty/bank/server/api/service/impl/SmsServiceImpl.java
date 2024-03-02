package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.StealVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.response.CheckVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.sms.response.SendVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.sms.response.StealVerificationCodeResponse;
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

        return new StealVerificationCodeResponse(otp);
    }

    @Override
    public SendVerificationCodeResponse sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest) {
        boolean isSent = verifyService.sendVerificationCode(
                sendVerificationCodeRequest.getPhoneNumber());

        if (!isSent) {
            // 인증번호 전송 실패
            throw new RuntimeException("인증번호 전송 실패");
        }

        return new SendVerificationCodeResponse("인증번호 전송 성공");
    }

    @Override
    public CheckVerificationCodeResponse checkVerificationCode(
            CheckVerificationCodeRequest checkVerificationCodeRequest) {
        boolean isValidVerificationCode = verifyService.checkVerificationCode(
                checkVerificationCodeRequest.getPhoneNumber(),
                checkVerificationCodeRequest.getVerificationCode());
        if (!isValidVerificationCode) {
            throw new RuntimeException("인증번호 검증 실패");
        }

        return new CheckVerificationCodeResponse("인증번호 검증 성공");
    }
}