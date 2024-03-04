package com.swifty.bank.server.api.controller.dto.auth.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignWithFormResponse {
    private boolean isSuccess;
    private List<String> tokens;
}