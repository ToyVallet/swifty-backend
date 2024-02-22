package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.CustomerAPIService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.common.service.JwtService;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.dto.PasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "customer")
@Tag(name = "Customer Information API")
@Slf4j
public class CustomerController {
    private final CustomerAPIService customerAPIService;
    private final JwtService jwtService;

    @GetMapping("")
    @Operation(summary = "get customer's whole information in database", description = "no request body needed")
    public ResponseEntity<?> customerInfo() {
        UUID customerId = jwtService.getCustomerId();

        ResponseResult<?> customerInfo = customerAPIService.getCustomerInfo(customerId);

        return ResponseEntity
                .ok()
                .body(customerInfo);
    }

    @PatchMapping("")
    @Operation(summary = "change customer's whole information in database", description = "specific DTO required")
    public ResponseEntity<?> customerInfoUpdate(@RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerId = jwtService.getCustomerId();

        ResponseResult<?> responseResult = customerAPIService.customerInfoUpdate(customerId,
                customerInfoUpdateCondition);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PostMapping("password")
    @Operation(summary = "confirm whether input and original password matches", description = "password string needed")
    public ResponseEntity<?> passwordConfirm(@RequestBody PasswordRequest password) {
        UUID customerId = jwtService.getCustomerId();

        ResponseResult responseResult = customerAPIService.confirmPassword(customerId, password.getPasswd());

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PatchMapping("password")
    @Operation(summary = "reset password with input", description = "password string needed in body")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordRequest newPassword) {
        UUID customerId = jwtService.getCustomerId();

        ResponseResult responseResult = customerAPIService.resetPassword(customerId, newPassword.getPasswd());

        return ResponseEntity
                .ok()
                .body(responseResult);
    }
}