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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneAuthenticationServiceImpl implements PhoneAuthenticationService {
    private final TwilioMessageService smsService;
    private final TwilioVerifyService verifyService;

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
        }
        return new ResponseResult<>(
                Result.SUCCESS,
                checkVerificationStatus.getStatus(),
                null
        );
    }
}
