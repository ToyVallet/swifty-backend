package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.CustomerAPIService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.utils.JwtUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "customer")
public class CustomerController {
    private final CustomerAPIService customerAPIService;
    private final JwtUtil jwtUtil;

    @GetMapping("")
    public ResponseEntity<?> customerInfo(@RequestHeader("Authorization") String jwt) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());

        ResponseResult<?> customerInfo = customerAPIService.getCustomerInfo(customerUuid);

        return ResponseEntity
                .ok()
                .body(customerInfo);
    }

    @PatchMapping("")
    public ResponseEntity<?> customerInfoUpdate(@RequestHeader("Authorization") String jwt,
                                                @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());
        ResponseResult<?> responseResult = customerAPIService.customerInfoUpdate(customerUuid,
                customerInfoUpdateCondition);
        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PostMapping("password")
    public ResponseEntity<?> passwordConfirm(@RequestHeader("Authorization") String jwt,
                                             @RequestBody String password) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());

        ResponseResult responseResult = customerAPIService.confirmPassword(customerUuid, password);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PatchMapping("password")
    public ResponseEntity<?> passwordReset(@RequestHeader("Authorization") String jwt,
                                           @RequestBody String newPassword) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());

        ResponseResult responseResult = customerAPIService.resetPassword(customerUuid, newPassword);
        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @DeleteMapping("")
    public ResponseEntity<?> customerWithdrawal(@RequestHeader("Authorization") String jwt) {
        UUID customerUuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", jwt).toString());

        ResponseResult responseResult = customerAPIService.customerWithdrawal(customerUuid);
        return ResponseEntity
                .ok()
                .body(responseResult);
    }

}