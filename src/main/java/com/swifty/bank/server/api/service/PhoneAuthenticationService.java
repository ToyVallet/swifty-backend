package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.sms.service.dto.SendMessageRequest;

public interface PhoneAuthenticationService {
    ResponseResult<?> sendMessage(SendMessageRequest sendMessageRequest);
}
