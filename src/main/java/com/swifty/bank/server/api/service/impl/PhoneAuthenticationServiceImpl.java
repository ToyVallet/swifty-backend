package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.PhoneAuthenticationService;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.sms.constant.CheckVerificationStatus;
import com.swifty.bank.server.core.domain.sms.constant.MessageStatus;
import com.swifty.bank.server.core.domain.sms.constant.SendVerificationStatus;
import com.swifty.bank.server.core.domain.sms.service.dto.CheckVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.SendMessageRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.SendVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.impl.TwilioMessageService;
import com.swifty.bank.server.core.domain.sms.service.impl.TwilioVerifyService;
import com.swifty.bank.server.utils.HashUtil;
import com.swifty.bank.server.utils.RedisUtil;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneAuthenticationServiceImpl implements PhoneAuthenticationService {
    private final TwilioMessageService smsService;
    private final TwilioVerifyService verifyService;
    private final RedisUtil redisUtil;

    @Override
    public ResponseResult<?> sendMessage(SendMessageRequest sendMessageRequest) {
        MessageStatus messageStatus = smsService.sendMessage(
                sendMessageRequest.getDestinationPhoneNumber(),
                sendMessageRequest.getSmsMessage()
        );

        if (messageStatus.equals(MessageStatus.FAILED)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "not sent",
                    null
            );
        }
        return new ResponseResult<>(
                Result.SUCCESS,
                "sent",
                null
        );
    }

    @Override
    public ResponseResult<?> sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest) {
        SendVerificationStatus sendVerificationStatus = verifyService.sendVerificationCode(
                sendVerificationCodeRequest.getPhoneNumber());
        String redisKey = createRedisOtpKey(sendVerificationCodeRequest.getPhoneNumber());
        redisUtil.setRedisStringValue(
                redisKey,
                "false"
        );
        redisUtil.setRedisStringExpiration(redisKey, 10, TimeUnit.MINUTES);

        if (sendVerificationStatus.equals(SendVerificationStatus.CANCELED)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "canceled",
                    null
            );
        }
        return new ResponseResult<>(
                Result.SUCCESS,
                sendVerificationStatus.getStatus(),
                null
        );
    }

    @Override
    public ResponseResult<?> checkVerificationCode(CheckVerificationCodeRequest checkVerificationCodeRequest) {
        CheckVerificationStatus checkVerificationStatus = verifyService.checkVerificationCode(
                checkVerificationCodeRequest.getPhoneNumber(),
                checkVerificationCodeRequest.getVerificationCode());

        if (checkVerificationStatus.equals(CheckVerificationStatus.CANCELED)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "canceled",
                    null
            );
        } else if (checkVerificationStatus.equals(CheckVerificationStatus.PENDING)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "invalid verification code",
                    null
            );
        }

        String redisKey = createRedisOtpKey(checkVerificationCodeRequest.getPhoneNumber());
        String isVerified = redisUtil.getRedisStringValue(redisKey);
        if (isVerified == null || isVerified.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "invalid phoneNumber or expired",
                    null
            );
        }

        redisUtil.setRedisStringValue(
                redisKey,
                "true"
        );
        log.info(redisKey + ": true");
        // 인증 성공한 시기로부터 10분간 인증 유효
        redisUtil.setRedisStringExpiration(redisKey, 10, TimeUnit.MINUTES);

        return new ResponseResult<>(
                Result.SUCCESS,
                checkVerificationStatus.getStatus(),
                null
        );
    }

    public String createRedisOtpKey(String str) {
        return HashUtil.createStringHash(
                List.of("otp-", str)
        );
    }
}
