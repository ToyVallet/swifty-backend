package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.*;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByDeviceID;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByPhoneNumberException;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "auth")
public class CustomerController {
    private final CustomerService customerService;
    private final JwtTokenUtil tokenUtil;
    private final BCryptPasswordEncoder encoder;

    @PostMapping(value = "sign-in")
    public ResponseEntity<?> login(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody CustomerLoginWithDeviceIDDto body
            ) {
        Map<String, Object> result = new HashMap<>( );
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity
                    .badRequest()
                    .body("[JWT ERROR] JWT Token format is not valid");
        }
        token = token.substring(7);
        if (!tokenUtil.validateToken(token)) {
            return ResponseEntity
                    .badRequest( )
                    .body("[JWT ERROR] JWT Token context is not valid");
        }

        UUID uuid = UUID.fromString(tokenUtil.getUuidFromToken(token));
        Customer customer = customerService.findByDeviceID(new CustomerFindByDeviceIDDto(
                body.getDeviceID( ))
        );

        if (uuid.toString( ).equals(customer.getId( ).toString( ))
         && customer.getDeviceID().equals(body.getDeviceID( ))) {
            result.put("token", tokenUtil.generateToken(customer));
            return ResponseEntity
                    .ok()
                    .body(result);
        }

        return ResponseEntity
                .badRequest( )
                .body("[ERROR] Token's UUID and device's original UUID is different");
    }

    @PostMapping("")
    public ResponseEntity<?> authWithForm(
            @RequestBody CustomerJoinDto body
    ) {
        Map<String, Object> res = new HashMap<>( );


        Customer customerByDeviceID;
        Customer customerByPhoneNumber;

        try {
            customerByDeviceID = customerService
                    .findByDeviceID(new CustomerFindByDeviceIDDto(body.getDeviceID()));
        }
        catch (NoSuchCustomerByDeviceID e) {
            customerByDeviceID = null;
        }

        try {
            customerByPhoneNumber = customerService
                    .findByPhoneNumber(
                            new CustomerFindByPhoneNumberDto(body.getPhoneNumber( ))
                    );
        }
        catch (NoSuchCustomerByPhoneNumberException e) {
            customerByPhoneNumber = null;
        }

        Customer customer = null;
        if (customerByPhoneNumber == null && customerByDeviceID == null) {
            CustomerJoinDto dto = new CustomerJoinDto(
                    body.getUuid(),
                    body.getName(),
                    body.getNationality(),
                    body.getPhoneNumber(),
                    encoder.encode(body.getPassword()),
                    body.getDeviceID()
            );
            customer = customerService.join(dto);
        }
        else if (customerByPhoneNumber == null) {
            CustomerUpdatePhoneNumberDto dto = new CustomerUpdatePhoneNumberDto(
                    body.getUuid(), body.getPhoneNumber( )
            );
            customer = customerService.updatePhoneNumber(dto);
        }
        else if (customerByDeviceID == null) {
            CustomerUpdateDeviceIDDto dto = new CustomerUpdateDeviceIDDto(
                    body.getUuid( ), body.getDeviceID( )
            );
            customer = customerService.updateDeviceID(dto);
        }
        else {
            if (customerByDeviceID.getId( ).equals(customerByPhoneNumber.getId( ))) {
                customer = customerByDeviceID;
            }
            else {

                customer = customerService.updateDeviceID(new CustomerUpdateDeviceIDDto(
                        customerByPhoneNumber.getId( ),
                        customerByPhoneNumber.getDeviceID( )
                ));
                customerService.updateDeviceID(new CustomerUpdateDeviceIDDto(
                        customerByDeviceID.getId( ),
                        "LOGOUT"
                ));
            }
        }

        res.put("token", tokenUtil.generateToken(customer));
        return ResponseEntity
                .ok( )
                .body(res);
    }
}