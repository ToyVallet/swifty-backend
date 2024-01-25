package com.swifty.bank.server.src.main.api.controller;

import com.swifty.bank.server.src.main.core.customer.dto.CustomerJoinDto;
import com.swifty.bank.server.src.main.core.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "customer")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping(value = "")
    public ResponseEntity join(@RequestBody CustomerJoinDto customerJoinDto) {
        try {
            customerService.join(customerJoinDto);
            return new ResponseEntity("회원가입 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("회원가입 실패", HttpStatus.OK);
        }
    }
}