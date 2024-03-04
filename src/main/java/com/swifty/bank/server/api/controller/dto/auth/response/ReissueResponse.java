package com.swifty.bank.server.api.controller.dto.auth.response;

import java.util.List;
import lombok.Builder;

@Builder
public class ReissueResponse {
    private boolean isSuccess;
    private List<String> tokens;
}
