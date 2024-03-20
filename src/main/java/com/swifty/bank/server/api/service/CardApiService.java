package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.card.request.CreateCardRequest;
import org.springframework.http.ResponseEntity;

public interface CardApiService {
    void createCard(String accessToken, String keypadToken,CreateCardRequest createCardRequest);
}
