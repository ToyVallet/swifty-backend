package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.keypad.response.CreateSecureKeypadResponse;

public interface SecureKeypadService {
    CreateSecureKeypadResponse createSecureKeypad(String temporaryToken);
}