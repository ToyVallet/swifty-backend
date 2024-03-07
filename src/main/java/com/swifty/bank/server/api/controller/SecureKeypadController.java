package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.api.controller.dto.keypad.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.service.SecureKeypadService;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/secure-keypad")
@Tag(name = "보안 키패드 관련 API")
@Slf4j
public class SecureKeypadController {
    private final SecureKeypadService secureKeypadService;

    @TemporaryAuth
    @GetMapping(value = "/create-keypad")
    @Operation(summary = "순서가 섞인 키패드 이미지 리스트 제공")
    public ResponseEntity<CreateSecureKeypadResponse> createSecureKeypad(
            @Parameter(description = "Authorization에 temporary token을 포함시켜 주세요", example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String temporaryToken
    ) {
        CreateSecureKeypadResponse res = secureKeypadService.createSecureKeypad(JwtUtil.removePrefix(temporaryToken));

        return ResponseEntity
                .ok()
                .body(res);
    }
}