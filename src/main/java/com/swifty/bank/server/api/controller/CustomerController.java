package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
import com.swifty.bank.server.core.utils.CookieUtils;
import com.swifty.bank.server.core.utils.DateUtil;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/customer")
@Tag(name = "회원 API")
@Slf4j
public class CustomerController {
    private final CustomerApiService customerApiService;

    @CustomerAuth
    @GetMapping("")
    @Operation(summary = "회원정보 조회", description = "access token에 대응되는 회원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 조회에 성공한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerInfoResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "회원이 존재하지 않음",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<CustomerInfoResponse> getCustomerInfo(
            @CookieValue("access-token") String accessToken
    ) {
        try {
            CustomerInfoResponse customerInfo = customerApiService.getCustomerInfo(JwtUtil.removeType(accessToken));

            return ResponseEntity
                    .ok()
                    .body(customerInfo);
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }

    @CustomerAuth
    @PatchMapping("")
    @Operation(summary = "회원정보 수정", description = "access token에 대응되는 회원의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 수정에 성공한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "회원정보 수정을 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<MessageResponse> customerInfoUpdate(
            @CookieValue("access-token") String accessToken,
            @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        try {
            customerApiService.customerInfoUpdate(JwtUtil.removeType(accessToken), customerInfoUpdateCondition);

            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("회원정보가 수정되었습니다."));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("회원정보 수정에 실패했습니다."));
        }
    }

    @CustomerAuth
    @PostMapping("/validate-password")
    @Operation(summary = "회원 비밀번호 일치여부 확인", description = "access token에 대응되는 회원의 비밀번호와 일치하는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 일치한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "access token에 올바르지 않은 customerUuid가 포함되어 있거나, 비밀번호가 불일치한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<MessageResponse> confirmPassword(
            @CookieValue("access-token") String accessToken,
            @CookieValue("keypad-token") String keypadToken,
            @RequestBody PasswordRequest password
    ) {
        try {
            boolean isMatchPassword = customerApiService.confirmPassword(
                    JwtUtil.removeType(accessToken),
                    JwtUtil.removeType(keypadToken),
                    password);
            if (isMatchPassword) {
                return ResponseEntity
                        .ok()
                        .header(
                                HttpHeaders.SET_COOKIE,
                                CookieUtils.createCookie("keypad-token",
                                        keypadToken,
                                        0L  // 사용을 다했으니 만료 시키기
                                ).toString()
                        )
                        .body(new MessageResponse("비밀번호가 일치합니다."));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("회원조회에 실패했습니다."));
        }

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("비밀번호가 불일치합니다."));
    }

    @CustomerAuth
    @PatchMapping("/update-password")
    @Operation(summary = "회원 비밀번호 변경", description = "access token에 대응되는 회원의 비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경에 성공한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "비밀번호 변경에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<MessageResponse> resetPassword(
            @CookieValue("access-token") String accessToken,
            @CookieValue("keypad-token") String keypadToken,
            @RequestBody PasswordRequest passwordRequest) {
        try {
            customerApiService.resetPassword(
                    JwtUtil.removeType(accessToken),
                    JwtUtil.removeType(keypadToken),
                    passwordRequest);

            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.SET_COOKIE,
                            CookieUtils.createCookie("keypad-token",
                                    keypadToken,
                                    0L  // 사용을 다했으니 만료 시키기
                            ).toString()
                    )
                    .body(new MessageResponse("비밀번호 변경에 성공했습니다."));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("비밀번호 변경에 실패했습니다."));
        }
    }

    @TemporaryAuth
    @GetMapping(value = "/create-keypad")
    @Operation(summary = "개인 식별 비밀번호 확인을 위한 키패드 이미지 제공", description = "순서가 섞인 키패드 이미지 리스트를 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키패드 이미지 리스트",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CreateSecureKeypadResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "클라이언트의 요청은 유효하나 서버가 처리에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<CreateSecureKeypadResponse> createSecureKeypad(
            @CookieValue("access-token") String accessToken
    ) {
        CreateSecureKeypadResponse res
                = customerApiService.createSecureKeypad();

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.createCookie(
                                "keypad-token",
                                res.getKeypadToken(),
                                DateUtil.diffInSeconds(DateUtil.now(), JwtUtil.getExpireDate(res.getKeypadToken()))
                        ).toString()
                )
                .body(res);
    }
}