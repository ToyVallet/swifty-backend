package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByDeviceID;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByPhoneNumberException;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationApiServiceImpl implements AuthenticationApiService {
    private final CustomerService customerService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public ResponseResult<?> join(JoinRequest dto) {
        Map<String, Object> result = new HashMap<>( );

        try {
            customerService
                    .findByPhoneNumber(dto.getPhoneNumber( ));
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[ERROR] enrolled user with this phone number exists, cannot sign up",
                    result
            );
        }
        catch (NoSuchCustomerByPhoneNumberException e) {
            // pass if there is no user enrolled with this phone number
        }

        Customer customer = customerService.join(dto);
        Customer customerByDeviceID;

        try {
            customerByDeviceID = customerService
                    .findByDeviceID(dto.getDeviceID( ));
        }
        catch (NoSuchCustomerByDeviceID e) {
            customerByDeviceID = null;
        }

        if (customerByDeviceID != null) {
            customer = customerService.updateDeviceID(
                    customerByDeviceID.getId(),
                    null
            );
        }


        result.put("token", jwtTokenUtil.generateToken(customer));
        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] New user enrolled with user id: " + customer.getId(),
                result
        );
    }

    @Override
    public ResponseResult<?> loginWithJwt(UUID uuid, String deviceId) {
        Map<String, Object> result = new HashMap<>( );
        Customer customer;
        try {
            customer = customerService.findByDeviceID(deviceId);
        }
        catch (NoSuchCustomerByDeviceID e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[ERROR] there is no device logged in with device " + deviceId,
                    result
            );
        }


        if (uuid.toString( ).equals(customer.getId( ).toString( ))
                && customer.getDeviceID().equals(deviceId)) {
            result.put("token", jwtTokenUtil.generateToken(customer));
            return new ResponseResult(Result.SUCCESS,
                    "[INFO] " + customer.getId( ) + "issued Token",
                    result
            );
        }

        return new ResponseResult(Result.FAIL,
                "[ERROR] Latest user of device is not match with token. It might be hijacked",
                result
        );
    }

    @Override
    public ResponseResult<?> loginWithForm(String deviceId, String phoneNumber) {
        Map<String, Object> res = new HashMap<>( );


        Customer customerByDeviceID;
        Customer customerByPhoneNumber;

        try {
            customerByDeviceID = customerService
                    .findByDeviceID(deviceId);
        }
        catch (NoSuchCustomerByDeviceID e) {
            customerByDeviceID = null;
        }

        try {
            customerByPhoneNumber = customerService
                    .findByPhoneNumber(phoneNumber);
        }
        catch (NoSuchCustomerByPhoneNumberException e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[ERROR] No registered user with phone number, cannot login",
                    res
            );
        }

        if (customerByDeviceID == null) {
            customerByPhoneNumber = customerService.updateDeviceID(
                    customerByPhoneNumber.getId(),
                    deviceId
            );
        }
        else {
            if (!customerByDeviceID.getId( ).equals(customerByPhoneNumber.getId( ))) {
                customerService.updateDeviceID(
                        customerByPhoneNumber.getId( ),
                        deviceId
                );
                customerService.updateDeviceID(
                        customerByDeviceID.getId( ),
                        null
                );
            }
        }

        res.put("token", jwtTokenUtil.generateToken(customerByPhoneNumber));
        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] " + customerByPhoneNumber.getId() + " login with form",
                res
        );
    }

    @Override
    public ResponseResult<?> logout(UUID uuid) {
        return null;
    }
}
