package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.account.request.AccountRegisterRequest;
import com.swifty.bank.server.api.controller.dto.account.request.RetrieveBalanceWithCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountNicknameRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseUnitedAccountPasswordRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateDefaultCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateSubAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateUnitedAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.WithdrawUnitedAccountRequest;
import com.swifty.bank.server.api.controller.dto.account.response.*;
import com.swifty.bank.server.api.controller.dto.auth.response.SignOutResponse;
import com.swifty.bank.server.api.service.AccountApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/account")
public class AccountApiController {
    private final AccountApiService accountApiService;

    @CustomerAuth
    @PostMapping(value = "/register")
    @Operation(summary = "계좌를 만드는 API", description = "한 개의 통합 계좌 -> 여러개의 환을 가짐 ) 을 만듦")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccountRegisterResponse.class))
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
    public ResponseEntity<?> register(@RequestHeader("Authorization") String jwt,
                                      @RequestBody AccountRegisterRequest req) {
        AccountRegisterResponse res = accountApiService.register(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PatchMapping(value = "/update-nickname")
    @Operation(summary = "계좌의 별명을 변경하는 API", description = "통합 계좌의 별명을 변경함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateAccountNicknameResponse.class))
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
    public ResponseEntity<?> updateNickname(
            @RequestHeader("Authorization") String jwt,
            @RequestBody ReviseAccountNicknameRequest req
    ) {
        UpdateAccountNicknameResponse res = accountApiService.updateNickname(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PatchMapping(value = "/update-password")
    @Operation(summary = "계좌의 비밀번호를 변경", description = "통합 계좌의 4자리 비밀번호를 변경한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReviseUnitedAccountPasswordResponse.class))
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
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            ReviseUnitedAccountPasswordRequest req
    ) {
        ReviseUnitedAccountPasswordResponse res = accountApiService.updatePassword(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @GetMapping("/retrieve-balance")
    @Operation(summary = "특정 계좌의 잔액을 조회", description = "특정 유저가 선택한 통장과 환(자신의 것, 환은 존재)을 조회하는 경우")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RetrieveBalanceWithCurrencyResponse.class))
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
    public ResponseEntity<?> retrieveBalanceWithCurrency(
            @RequestHeader("authorization")
            String jwt,
            @RequestBody
            RetrieveBalanceWithCurrencyRequest req
    ) {
        RetrieveBalanceWithCurrencyResponse res = accountApiService.retrieveBalanceWithCurrency(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/withdraw")
    @Operation(summary = "계좌를 삭제하는 액션", description = "특정 계좌와 그 밑에 존재하는 모든 계좌(환)들을 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WithdrawUnitedAccountResponse.class))
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
    public ResponseEntity<?> withdrawAccount(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            WithdrawUnitedAccountRequest req
    ) {
        WithdrawUnitedAccountResponse res = accountApiService.withdraw(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/update-status")
    @Operation(summary = "통합 계좌를 수정하는 액션", description = "통합 계좌를 수정함 (상태) -> 단 그 아래에 딸려있는 환, 계좌번호 등은 수정 불가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateUnitedAccountStatusResponse.class))
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
    public ResponseEntity<?> updateUnitedAccount(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            UpdateUnitedAccountStatusRequest req
    ) {
        UpdateUnitedAccountStatusResponse res = accountApiService.updateUnitedAccountStatus(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/update-currency-status")
    @Operation(summary = "통합 계좌의 특정 환 상태를 바꾸는 액션", description = "특정 계좌의 개별 환 중 하나의 활성화 상태를 변경한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateSubAccountStatusResponse.class))
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
    public ResponseEntity<?> updateSubAccountStatus(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            UpdateSubAccountStatusRequest req
    ) {
        UpdateSubAccountStatusResponse res = accountApiService.updateSubAccountStatus(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping(value = "/update-default-currency")
    @Operation(summary = "통합 계좌의 대표환을 바꾸는 액션", description = "통합 계좌의 대표 환을 변경한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 변경한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateDefaultCurrencyResponse.class))
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
    public ResponseEntity<?> updateSubAccountStatus(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            UpdateDefaultCurrencyRequest req
    ) {
        UpdateDefaultCurrencyResponse res = accountApiService.updateDefaultCurrency(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @GetMapping(value = "/list")
    @Operation(summary = "특정 사용자가 가진 계좌들을 나열", description = "특정 사용자가 가진 계좌들을 나열한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListUnitedAccountWithCustomerResponse.class))
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
    public ResponseEntity<?> listUnitedAccountWithCustomer(
            @RequestHeader("Authorization")
            String jwt
    ) {
        ListUnitedAccountWithCustomerResponse res = accountApiService.listUnitedAccountWithCustomer(jwt);

        return ResponseEntity
                .ok()
                .body(res);
    }
}
