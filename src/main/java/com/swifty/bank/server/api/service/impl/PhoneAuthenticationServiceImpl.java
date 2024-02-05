package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.PhoneAuthenticationService;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.sms.constant.MessageStatus;
import com.swifty.bank.server.core.domain.sms.service.dto.SendMessageRequest;
import com.swifty.bank.server.core.domain.sms.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneAuthenticationServiceImpl implements PhoneAuthenticationService {
    private final MessageServiceImpl smsService;

    @Override
    public ResponseResult<?> sendMessage(SendMessageRequest sendMessageRequest) {
        MessageStatus messageStatus = smsService.sendMessage(
                sendMessageRequest.getDestinationPhoneNumber(),
                sendMessageRequest.getSmsMessage()
        );

        if (messageStatus.equals(MessageStatus.FAILED)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "메세지를 보내는 데 실패하였습니다.",
                    null
            );
        }
        return new ResponseResult<>(
                Result.SUCCESS,
                "메세지를 보내는데 성공하였습니다.",
                null
        );
    }
}
