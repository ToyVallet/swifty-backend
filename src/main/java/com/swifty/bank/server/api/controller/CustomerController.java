package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.core.customer.dto.CustomerFindDto;
import com.swifty.bank.server.core.customer.dto.CustomerJoinDto;
import com.swifty.bank.server.core.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

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

    // @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    @GetMapping(value = "find")
    public ResponseEntity find(@RequestBody CustomerFindDto uuid) {
        try {
            return new ResponseEntity(customerService.find(uuid), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.OK);
        }
    }

//    @GetMapping(value = "update")
//    public ResponseEntity update(@RequestBody CustomerJoinDto customerJoinDto) {
//        try {
//            return new ResponseEntity(customerService.updatePhoneNumber(customerJoinDto), HttpStatus.OK);
//        }
//        catch (Exception e) {
//            return new ResponseEntity(e.getMessage(), HttpStatus.OK);
//        }
//    }

    @GetMapping(value = "delete")
    public ResponseEntity delete(@RequestBody CustomerFindDto customerFindDto) {
        try {
            customerService.withdrawCustomer(customerFindDto);
            return new ResponseEntity("Successfully deleted", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.OK);
        }
    }

}