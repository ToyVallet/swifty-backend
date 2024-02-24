package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.impl.CustomerApiServiceImpl;
import com.swifty.bank.server.core.common.authentication.service.impl.AuthenticationServiceImpl;
import com.swifty.bank.server.core.common.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/customer")
@Tag(name = "Customer Information API")
@Slf4j
public class CustomerController {
    private final CustomerApiServiceImpl customerApiService;
    private final AuthenticationServiceImpl authenticationService;

    @GetMapping("")
    @Operation(summary = "get customer's whole information in database", description = "no request body needed")
    public ResponseEntity<?> customerInfo() {
        String jwt = JwtUtil.extractJwtFromCurrentRequestHeader();
        UUID customerId = UUID.fromString(JwtUtil.getClaimByKey(jwt, "customerId").toString());

        ResponseResult<?> customerInfo = customerApiService.getCustomerInfo(customerId);

        return ResponseEntity
                .ok()
                .body(customerInfo);
    }

    @PatchMapping("")
    @Operation(summary = "change customer's whole information in database", description = "specific DTO required")
    public ResponseEntity<?> customerInfoUpdate(
            @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        String jwt = JwtUtil.extractJwtFromCurrentRequestHeader();
        UUID customerId = UUID.fromString(JwtUtil.getClaimByKey(jwt, "customerId").toString());

        ResponseResult<?> responseResult = customerApiService.customerInfoUpdate(customerId,
                customerInfoUpdateCondition);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PostMapping("/password")
    @Operation(summary = "confirm whether input and original password matches", description = "password string needed")
    public ResponseEntity<?> passwordConfirm(@RequestBody PasswordRequest password) {
        String jwt = JwtUtil.extractJwtFromCurrentRequestHeader();
        UUID customerId = UUID.fromString(JwtUtil.getClaimByKey(jwt, "customerId").toString());

        ResponseResult responseResult = customerApiService.confirmPassword(customerId, password.getPasswd());

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PatchMapping("/password")
    @Operation(summary = "reset password with input", description = "password string needed in body")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordRequest newPassword) {
        String jwt = JwtUtil.extractJwtFromCurrentRequestHeader();
        UUID customerId = UUID.fromString(JwtUtil.getClaimByKey(jwt, "customerId").toString());

        ResponseResult responseResult = customerApiService.resetPassword(customerId, newPassword.getPasswd());

        return ResponseEntity
                .ok()
                .body(responseResult);
    }
}