package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.api.controller.dto.keypad.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.service.SecureKeypadService;
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
    @Operation(summary = "셔플된 키패드 이미지 제공")
    public ResponseEntity<CreateSecureKeypadResponse> createSecureKeyPad(
            @Parameter(description = "Authorization 헤더에 temporary token을 포함시켜주세요", example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String temporaryToken
    ) {
        CreateSecureKeypadResponse res = secureKeypadService.createSecureKeypad(temporaryToken);

        return ResponseEntity
                .ok()
                .body(res);
    }
}