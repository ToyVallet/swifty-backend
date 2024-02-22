package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.GetVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.service.PhoneAuthenticationService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.common.utils.RandomUtil;
import com.swifty.bank.server.core.common.utils.RedisUtil;
import com.swifty.bank.server.core.domain.sms.service.impl.VerifyServiceImpl;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneAuthenticationServiceImpl implements PhoneAuthenticationService {
    private VerifyServiceImpl verifyService;
    private final RedisUtil redisUtil;

    @Override
    public ResponseResult<?> stealVerificationCode(GetVerificationCodeRequest getVerificationCodeRequest) {
        String otp = RandomUtil.generateOtp(6);

        String redisKey = verifyService.createRedisKeyForOtp(getVerificationCodeRequest.getPhoneNumber());
        redisUtil.setRedisStringValue(
                redisKey,
                otp
        );
        redisUtil.setRedisStringExpiration(redisKey, 5, TimeUnit.MINUTES);

        return new ResponseResult<>(
                Result.SUCCESS,
                "otp is",
                otp
        );
    }

    @Override
    public ResponseResult<?> sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest) {
        boolean isSent = verifyService.sendVerificationCode(
                sendVerificationCodeRequest.getPhoneNumber());

        if (!isSent) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "인증번호 전송이 실패하였습니다.",
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "인증번호 전송이 성공하였습니다.",
                null
        );
    }

    @Override
    public ResponseResult<?> checkVerificationCode(CheckVerificationCodeRequest checkVerificationCodeRequest) {
        boolean isValidVerificationCode = verifyService.checkVerificationCode(
                checkVerificationCodeRequest.getPhoneNumber(),
                checkVerificationCodeRequest.getVerificationCode());
        if (!isValidVerificationCode) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "인증에 실패하였습니다.",
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "인증에 성공하였습니다.",
                null
        );
    }
}