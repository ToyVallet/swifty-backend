package com.swifty.bank.server.api.controller.dto.auth.response;

import lombok.Builder;

@Builder
public class SignoutResponse {
    private final Boolean wasSignedOut;
}
