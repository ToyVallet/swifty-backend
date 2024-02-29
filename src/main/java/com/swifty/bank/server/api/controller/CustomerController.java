package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
import com.swifty.bank.server.api.service.dto.Response;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/customer")
@Tag(name = "Customer Information API")
@Slf4j
public class CustomerController {
    private final CustomerApiService customerApiService;
    private JwtUtil jwtUtil;

    @GetMapping("")
    @Operation(summary = "get customer's whole information in database", description = "no request body needed")
    public ResponseEntity<?> customerInfo() {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        CustomerInfoResponse customerInfo = customerApiService.getCustomerInfo(customerId);

        return Response.SUCCESS.setData(customerInfo);
    }

    @PatchMapping("")
    @Operation(summary = "change customer's whole information in database", description = "specific DTO required")
    public ResponseEntity<?> customerInfoUpdate(
            @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        customerApiService.customerInfoUpdate(customerId, customerInfoUpdateCondition);

        return Response.SUCCESS.get();
    }

    @PostMapping("/password")
    @Operation(summary = "confirm whether input and original password matches", description = "password string needed")
    public ResponseEntity<?> passwordConfirm(@RequestBody PasswordRequest password) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        boolean isSamePassword = customerApiService.confirmPassword(customerId, password.getPasswd());

        if (isSamePassword) {
            return Response.SUCCESS.setMessage("비밀번호가 일치합니다.");
        }

        return Response.FAIL.setMessage("비밀번호가 일치하지 않습니다.");
    }

    @PatchMapping("/password")
    @Operation(summary = "reset password with input", description = "password string needed in body")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordRequest newPassword) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        customerApiService.resetPassword(customerId, newPassword.getPasswd());

        return Response.SUCCESS.setMessage("비밀번호를 변경하였습니다.");
    }
}