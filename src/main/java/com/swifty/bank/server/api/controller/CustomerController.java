package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
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

    @GetMapping("")
    @Operation(summary = "get customer's whole information in database", description = "no request body needed")
    public ResponseEntity<?> customerInfo() {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        CustomerInfoResponse customerInfo = customerApiService.getCustomerInfo(customerId);

        return ResponseEntity
                .ok()
                .body(customerInfo);
    }

    @PatchMapping("")
    @Operation(summary = "change customer's whole information in database", description = "specific DTO required")
    public ResponseEntity<?> customerInfoUpdate(
            @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        customerApiService.customerInfoUpdate(customerId, customerInfoUpdateCondition);

        return ResponseEntity
                .ok()
                .body(new MessageResponse("회원정보를 수정하였습니다."));
    }

    @PostMapping("/password")
    @Operation(summary = "confirm whether input and original password matches", description = "password string needed")
    public ResponseEntity<?> passwordConfirm(@RequestBody PasswordRequest password) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        boolean isSamePassword = customerApiService.confirmPassword(customerId, password.getPassword());

        if (isSamePassword) {
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("비밀번호가 일치합니다."));

        }

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("비밀번호가 일치하지 않습니다."));
    }

    @PatchMapping("/password")
    @Operation(summary = "reset password with input", description = "password string needed in body")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordRequest newPassword) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(JwtUtil.extractJwtFromCurrentRequestHeader(), "customerId", UUID.class);

        customerApiService.resetPassword(customerId, newPassword.getPassword());

        return ResponseEntity
                .ok()
                .body(new MessageResponse("비밀번호 변경을 완료하였습니다."));
    }
}