package com.swifty.bank.server.core.domain.keypad.service;

import com.swifty.bank.server.core.domain.keypad.service.dto.SecureKeypadDto;

public interface SecureKeypadService {
    SecureKeypadDto createSecureKeypad();

    String createKeypadToken();
}
