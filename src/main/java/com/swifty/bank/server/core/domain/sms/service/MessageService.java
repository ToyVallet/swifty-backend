package com.swifty.bank.server.core.domain.sms.service;

import com.swifty.bank.server.core.domain.sms.constant.MessageStatus;

public interface MessageService {
    MessageStatus sendMessage(String destinationNumber, String smsMessage);
}
