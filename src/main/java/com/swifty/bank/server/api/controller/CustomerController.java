package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/customer")
@Tag(name = "Customer Information API")
@Slf4j
public class CustomerController {
    private final CustomerApiService customerApiService;
    private final JwtUtil jwtUtil;

    @CustomerAuth
    @GetMapping("")
    @Operation(summary = "get customer's whole information in database", description = "no request body needed")
    public ResponseEntity<?> customerInfo() {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);
        CustomerInfoResponse customerInfo = customerApiService.getCustomerInfo(customerId);

        return ResponseEntity
                .ok()
                .body(customerInfo);
    }

    @CustomerAuth
    @PatchMapping("")
    @Operation(summary = "change customer's whole information in database", description = "specific DTO required")
    public ResponseEntity<?> customerInfoUpdate(
            @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        customerApiService.customerInfoUpdate(customerId,
                customerInfoUpdateCondition);

        return ResponseEntity
                .ok()
                .body(new MessageResponse("회원정보를 수정하였습니다."));
    }

    @CustomerAuth
    @PostMapping("/password")
    @Operation(summary = "confirm whether input and original password matches", description = "password string needed")
    public ResponseEntity<?> passwordConfirm(@RequestBody PasswordRequest password) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        boolean isMatchPassword = customerApiService.confirmPassword(customerId, password.getPassword());

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
    @Operation(summary = "reset password with input", description = "password string needed in body")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordRequest newPassword) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        customerApiService.resetPassword(customerId, newPassword.getPassword());

        return ResponseEntity
                .ok()
                .body(new MessageResponse("비밀번호를 변경하였습니다."));
    }
}