package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.CustomerAPIService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.dto.PasswordRequest;
import com.swifty.bank.server.utils.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "customer")
@Tag(name = "Customer Information API")
public class CustomerController {
    private final CustomerAPIService customerAPIService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "get customer's whole information in database", description = "no request body needed")
    @GetMapping("")
    public ResponseEntity<?> customerInfo(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token
    ) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);

        ResponseResult<?> customerInfo = customerAPIService.getCustomerInfo(customerUuid);

        return ResponseEntity
                .ok()
                .body(customerInfo);
    }

    @Operation(summary = "change customer's whole information in database", description = "specific DTO required")
    @PatchMapping("")
    public ResponseEntity<?> customerInfoUpdate(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition
    ) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);
        ResponseResult<?> responseResult = customerAPIService.customerInfoUpdate(customerUuid, customerInfoUpdateCondition);
        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @Operation(summary = "confirm whether input and original password matches", description = "password string needed")
    @PostMapping("password")
    public ResponseEntity<?> passwordConfirm(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordRequest password
    ) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);

        ResponseResult responseResult = customerAPIService.confirmPassword(customerUuid, password.getPasswd());

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @Operation(summary = "reset password with input", description = "password string needed in body")
    @PatchMapping("password")
    public ResponseEntity<?> passwordReset(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordRequest newPassword
    ) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);

        ResponseResult responseResult = customerAPIService.resetPassword(customerUuid, newPassword.getPasswd());
        return ResponseEntity
                .ok()
                .body(responseResult);
    }
}