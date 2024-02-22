package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.PhoneAuthenticationService;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.sms.constant.MessageStatus;
import com.swifty.bank.server.core.domain.sms.service.dto.CheckVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.GetVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.SendVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.impl.TwilioMessageService;
import com.swifty.bank.server.utils.HashUtil;
import com.swifty.bank.server.utils.RedisUtil;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneAuthenticationServiceImpl implements PhoneAuthenticationService {
    private final TwilioMessageService messageService;
    private final RedisUtil redisUtil;

    @Override
    public ResponseResult<?> getVerificationCode(GetVerificationCodeRequest getVerificationCodeRequest) {
        String otp = generateOtp(6);

        String redisKey = createRedisOtpKey(getVerificationCodeRequest.getPhoneNumber());
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
        String otp = generateOtp(6);

        MessageStatus messageStatus = messageService.sendMessage(
                sendVerificationCodeRequest.getPhoneNumber(),
                "[SWIFTY 뱅크] 회원가입 인증번호는 " + otp + "입니다."
        );

        if (messageStatus.equals(MessageStatus.FAILED)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "message with verification code not sent",
                    null
            );
        }

        String redisKey = createRedisOtpKey(sendVerificationCodeRequest.getPhoneNumber());
        redisUtil.setRedisStringValue(
                redisKey,
                otp
        );
        redisUtil.setRedisStringExpiration(redisKey, 5, TimeUnit.MINUTES);

        return new ResponseResult<>(
                Result.SUCCESS,
                messageStatus.getStatus(),
                null
        );
    }

    @Override
    public ResponseResult<?> checkVerificationCode(CheckVerificationCodeRequest checkVerificationCodeRequest) {
        String redisKey = createRedisOtpKey(checkVerificationCodeRequest.getPhoneNumber());
        String actualOtp = redisUtil.getRedisStringValue(redisKey);
        if (actualOtp == null || actualOtp.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "invalid phone number or expired",
                    null
            );
        }

        String expectedOtp = checkVerificationCodeRequest.getVerificationCode();
        if (!actualOtp.equals(expectedOtp)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "invalid verification code",
                    null
            );
        }

        redisUtil.setRedisStringValue(
                redisKey,
                "true"
        );
        // 인증 성공한 시기로부터 10분간 인증 유효
        redisUtil.setRedisStringExpiration(redisKey, 10, TimeUnit.MINUTES);

        return new ResponseResult<>(
                Result.SUCCESS,
                "success verification",
                null
        );
    }

    public String createRedisOtpKey(String str) {
        return HashUtil.createStringHash(
                List.of("otp-", str)
        );
    }

    public String generateOtp(int len) {
        StringBuilder sb = new StringBuilder();
        long seed = System.currentTimeMillis();
        long salt = getRandomNumber(1, 99);
        Random random = new Random(seed + salt);
        for (int i = 0; i < len; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}