package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    @Operation(summary = "회원정보 조회", description = "jwt access 토큰으로 회원정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 정상 조회",
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
    public ResponseEntity<CustomerInfoResponse> customerInfo(
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
    @Operation(summary = "회원정보 수정", description = "jwt access 토큰과 CustomerInfoUpdateConditionRequest 회원정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보를 수정하였습니다.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "회원정보를 수정을 실패하였습니다..",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<MessageResponse> customerInfoUpdate(
            @Parameter(description = "Authorization에 AccessToken을 포함시켜 주세요", example = "Bearer ey...", required = true) @RequestHeader("Authorization") String accessToken,
            @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        accessToken = JwtUtil.removeType(accessToken);

        customerApiService.customerInfoUpdate(accessToken, customerInfoUpdateCondition);

        return ResponseEntity
                .ok()
                .body(new MessageResponse("회원정보를 수정하였습니다."));
    }

    @CustomerAuth
    @PostMapping("/password")
    @Operation(summary = "회원 비밀번호 일치여부 확인", description = "jwt access 토큰과 입력한 password로 비밀번호가 일치하는지 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 일치합니다.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "비밀번호가 불일치합니다.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<MessageResponse> passwordConfirm(@CookieValue("accessToken") String accessToken
            , @RequestBody PasswordRequest password) {
        accessToken = JwtUtil.removeType(accessToken);

        boolean isMatchPassword = customerApiService.confirmPassword(accessToken, password);

        if (isMatchPassword) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("비밀번호가 일치합니다."));
        }

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("비밀번호가 불일치합니다."));
    }

    @CustomerAuth
    @PatchMapping("/password")
    @Operation(summary = "회원 비밀번호 변경", description = "jwt access 토큰과 입력한 신규 비밀번호로 변경")
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
    public ResponseEntity<MessageResponse> resetPassword(@CookieValue("accessToken") String accessToken
            , @RequestBody PasswordRequest passwordRequest) {
        try {
            customerApiService.resetPassword(JwtUtil.removeType(accessToken), passwordRequest);

            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("비밀번호 변경에 성공했습니다."));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("비밀번호 변경에 실패했습니다."));
        }
    }
}