package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.CustomerAPIService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "customer")
public class CustomerController {
    private final CustomerAPIService customerAPIService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("")
    public ResponseEntity<?> customerInfo(@RequestHeader("Authorization") String token) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);

        ResponseResult<?> customerInfo = customerAPIService.getCustomerInfo(customerUuid);

        return ResponseEntity
                .ok( )
                .body(customerInfo);
    }
    @PatchMapping("")
    public ResponseEntity<?> customerInfoUpdate(@RequestHeader("Authorization") String token, @RequestBody CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);
        ResponseResult<?> responseResult = customerAPIService.customerInfoUpdate(customerUuid, customerInfoUpdateCondition);
        return ResponseEntity
                .ok( )
                .body(responseResult);
    }

    @PostMapping("password")
    public ResponseEntity<?> passwordConfirm(@RequestHeader("Authorization") String token, @RequestBody String password) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);

        ResponseResult responseResult = customerAPIService.confirmPassword(customerUuid,password);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PatchMapping("password")
    public ResponseEntity<?> passwordReset(@RequestHeader("Authorization") String token, @RequestBody String newPassword) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);

        ResponseResult responseResult = customerAPIService.resetPassword(customerUuid,newPassword);
        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @DeleteMapping("")
    public ResponseEntity<?> customerWithdrawal(@RequestHeader("Authorization") String token) {
        UUID customerUuid = jwtTokenUtil.getUuidFromToken(token);

        ResponseResult responseResult = customerAPIService.customerWithdrawal(customerUuid);
        return ResponseEntity
                .ok()
                .body(responseResult);
    }

}