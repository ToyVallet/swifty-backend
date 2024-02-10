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

        redisUtil.setRedisStringValue(
                sendVerificationCodeRequest.getDeviceId() + sendVerificationCodeRequest.getPhoneNumber(),
                "false"
        );

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

        String phoneAuthHash = HashUtil.createStringHash(
                List.of(checkVerificationCodeRequest.getDeviceId(),
                        checkVerificationCodeRequest.getPhoneNumber()));
        String isVerified = redisUtil.getRedisStringValue(phoneAuthHash);
        if (isVerified == null || isVerified.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "invalid deviceId or phoneNumber",
                    null
            );
        }

        redisUtil.setRedisStringValue(
                HashUtil.createStringHash(
                        List.of(checkVerificationCodeRequest.getDeviceId(),
                                checkVerificationCodeRequest.getPhoneNumber())), "true");
        return new ResponseResult<>(
                Result.SUCCESS,
                checkVerificationStatus.getStatus(),
                null
        );
    }
}
