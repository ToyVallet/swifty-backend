package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.*;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByDeviceID;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByPhoneNumberAndNationality;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CustomerController {
    private final CustomerService customerService;
    @Autowired
    private final JwtTokenUtil tokenUtil;
    private final BCryptPasswordEncoder encoder;

    @PostMapping(value = "sign_in")
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
        Customer customer = customerService.findByDeviceID(new CustomerFindByDeviceIDDto(body.getDeviceID( )));

        if (uuid.toString( ).equals(customer.getId( ).toString( ))
         && customer.getDeviceID().equals(encoder.encode(body.getDeviceID( )))) {
            result.put("token", tokenUtil.generateToken(customer));
            return ResponseEntity
                    .ok()
                    .body(result);
        }

        return ResponseEntity
                .badRequest( )
                .body("[ERROR] Token's UUID and device's original UUID is different");
    }

    @PostMapping(value = "auth")
    public ResponseEntity<?> authWithForm(
            @RequestBody CustomerJoinDto body
    ) {
        Map<String, Object> res = new HashMap<>( );

        String deviceID = encoder.encode(body.getDeviceID( ));

        Customer customerByDeviceID;
        Customer customerByPhoneNumberAndNationality;

        try {
            customerByDeviceID = customerService
                    .findByDeviceID(new CustomerFindByDeviceIDDto(deviceID));
        }
        catch (NoSuchCustomerByDeviceID e) {
            customerByDeviceID = null;
        }

        try {
            customerByPhoneNumberAndNationality = customerService
                    .findByPhoneNumberAndNationality(
                            new CustomerFindByPhoneNumberAndNationality(body.getPhoneNumber( ), body.getNationality( ))
                    );
        }
        catch (NoSuchCustomerByPhoneNumberAndNationality e) {
            customerByPhoneNumberAndNationality = null;
        }

        Customer customer = null;
        if (customerByPhoneNumberAndNationality == null && customerByDeviceID == null) {
            CustomerJoinDto dto = new CustomerJoinDto(
                    body.getUuid(),
                    body.getName(),
                    body.getNationality(),
                    body.getPhoneNumber(),
                    body.getPassword(),
                    deviceID
            );
            customer = customerService.join(dto);
        }
        else if (customerByPhoneNumberAndNationality == null) {
            CustomerUpdatePhoneNumberAndNationalityDto dto = new CustomerUpdatePhoneNumberAndNationalityDto(
                    body.getUuid(),  body.getNationality( ), body.getPhoneNumber( )
            );
            customer = customerService.updatePhoneNumberAndNationality(dto);
        }
        else if (customerByDeviceID == null) {
            CustomerUpdateDeviceIDDto dto = new CustomerUpdateDeviceIDDto(
                    body.getUuid( ), deviceID
            );
            customer = customerService.updateDeviceID(dto);
        }
        else {
            if (customerByDeviceID.getId( ).equals(customerByPhoneNumberAndNationality.getId( ))) {
                customer = customerByDeviceID;
            }
            else {

                customer = customerService.updateDeviceID(new CustomerUpdateDeviceIDDto(
                        customerByPhoneNumberAndNationality.getId( ),
                        encoder.encode(customerByPhoneNumberAndNationality.getDeviceID( ))
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


//    @GetMapping(value = "find")
//    public ResponseEntity find(@RequestBody CustomerFindDto uuid) {
//        try {
//            return new ResponseEntity(customerService.find(uuid), HttpStatus.OK);
//        }
//        catch (Exception e) {
//            return new ResponseEntity(e.getMessage(), HttpStatus.OK);
//        }
//    }
//
}