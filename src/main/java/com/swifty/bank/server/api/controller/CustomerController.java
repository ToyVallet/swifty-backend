package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.CustomerAPIService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.dto.PasswordRequest;
import com.swifty.bank.server.utils.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @GetMapping("")
    @Operation(summary = "get customer's whole information in database", description = "no request body needed")
    public ResponseEntity<?> customerInfo(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String jwt
    ) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());

        ResponseResult<?> customerInfo = customerAPIService.getCustomerInfo(customerUuid);

        return ResponseEntity
                .ok()
                .body(customerInfo);
    }

    @PatchMapping("")
    @Operation(summary = "change customer's whole information in database", description = "specific DTO required")
    public ResponseEntity<?> customerInfoUpdate(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String jwt,
            @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition
    ) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());
        ResponseResult<?> responseResult = customerAPIService.customerInfoUpdate(customerUuid,
                customerInfoUpdateCondition);
        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PostMapping("password")
    @Operation(summary = "confirm whether input and original password matches", description = "password string needed")
    public ResponseEntity<?> passwordConfirm(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String jwt,
            @RequestBody PasswordRequest password
    ) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());

        ResponseResult responseResult = customerAPIService.confirmPassword(customerUuid, password.getPasswd());

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PatchMapping("password")
    @Operation(summary = "reset password with input", description = "password string needed in body")
    public ResponseEntity<?> passwordReset(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String jwt,
            @RequestBody PasswordRequest newPassword
    ) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());

        ResponseResult responseResult = customerAPIService.resetPassword(customerUuid, newPassword.getPasswd());
        return ResponseEntity
                .ok()
                .body(responseResult);
    }
}