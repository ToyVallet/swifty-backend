package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.StealVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.response.CheckVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.sms.response.SendVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.sms.response.StealVerificationCodeResponse;
import com.swifty.bank.server.api.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sms")
@Tag(name = "SMS 인증 API")
@Slf4j
public class SmsController {
    private final SmsService smsService;

    @TemporaryAuth
    @PostMapping(value = "/steal-verification-code")
    @Operation(summary = "인증번호 훔쳐보기", description = "생성된 인증번호를 훔쳐봅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호가 성공적으로 발급된 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StealVerificationCodeResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "요청 폼이 잘못된 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "클라이언트의 요청은 유효한데 서버가 처리에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<StealVerificationCodeResponse> stealVerificationCode(
            @RequestBody @Valid StealVerificationCodeRequest stealVerificationCodeRequest) {
        StealVerificationCodeResponse res = smsService.stealVerificationCode(
                stealVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @TemporaryAuth
    @PostMapping(value = "/send-verification-code")
    @Operation(summary = "인증번호 발송", description = "요청받은 전화번호로 인증번호를 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호가 정상적으로 발송된 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SendVerificationCodeResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "요청 폼이 잘못된 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "클라이언트의 요청은 유효한데 서버가 처리에 실패한 경우(전화번호가 잘못된 경우, Twilio 서비스에서 문자 전송이 실패한 경우 등)",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })

    })
    public ResponseEntity<SendVerificationCodeResponse> sendVerificationCode(
            @RequestBody @Valid SendVerificationCodeRequest sendVerificationCodeRequest) {
        SendVerificationCodeResponse res = smsService.sendVerificationCode(
                sendVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @TemporaryAuth
    @PostMapping(value = "/check-verification-code")
    @Operation(summary = "인증번호 검증", description = "인증번호를 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호가 일치한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CheckVerificationCodeResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "요청 폼이 잘못된 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "클라이언트의 요청은 유효한데 서버가 처리에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<CheckVerificationCodeResponse> checkVerificationCode(
            @RequestBody @Valid CheckVerificationCodeRequest checkVerificationCodeRequest) {
        CheckVerificationCodeResponse res = smsService.checkVerificationCode(
                checkVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(res);
    }
}