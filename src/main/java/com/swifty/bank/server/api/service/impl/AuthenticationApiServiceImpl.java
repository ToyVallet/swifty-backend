package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.exceptions.CannotReferCustomerByNullException;
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
        Map<String, Object> result = new HashMap<>();

        try {
            customerService
                    .findByPhoneNumber(dto.getPhoneNumber());
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[ERROR] enrolled user with this phone number exists, cannot sign up",
                    result
            );
        } catch (NoSuchCustomerByPhoneNumberException e) {
            // pass if there is no user enrolled with this phone number
        } catch (CannotReferCustomerByNullException e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    e.getMessage(),
                    null
            );
        }

        Customer customerByDeviceId;

        try {
            customerByDeviceId = customerService
                    .findByDeviceId(dto.getDeviceId());
        } catch (NoSuchCustomerByDeviceID e) {
            customerByDeviceId = null;
        } catch (CannotReferCustomerByNullException e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    e.getMessage(),
                    null
            );
        }

        Customer customer = customerService.join(dto);
        if (customerByDeviceId != null) {
            customerService.updateDeviceId(
                    customerByDeviceId.getId(),
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
        Map<String, Object> result = new HashMap<>();
        Customer customer;
        try {
            customer = customerService.findByDeviceId(deviceId);
        } catch (NoSuchCustomerByDeviceID e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[ERROR] there is no device logged in with device " + deviceId,
                    result
            );
        } catch (CannotReferCustomerByNullException e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    e.getMessage(),
                    null
            );
        }

        if (uuid.toString().equals(customer.getId().toString())
                && customer.getDeviceId().equals(deviceId)) {
            result.put("token", jwtTokenUtil.generateToken(customer));
            return new ResponseResult(Result.SUCCESS,
                    "[INFO] " + customer.getId() + "issued Token",
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
        Map<String, Object> res = new HashMap<>();


        Customer customerByDeviceID;
        Customer customerByPhoneNumber;

        try {
            customerByDeviceID = customerService
                    .findByDeviceId(deviceId);
        } catch (NoSuchCustomerByDeviceID e) {
            customerByDeviceID = null;
        } catch (CannotReferCustomerByNullException e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    e.getMessage(),
                    null
            );
        }

        try {
            customerByPhoneNumber = customerService
                    .findByPhoneNumber(phoneNumber);
        } catch (NoSuchCustomerByPhoneNumberException e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[ERROR] No registered user with phone number, cannot login",
                    res
            );
        } catch (CannotReferCustomerByNullException e) {
            return new ResponseResult<>(
                    Result.SUCCESS,
                    e.getMessage(),
                    null
            );
        }

        if (customerByDeviceID == null) {
            customerByPhoneNumber = customerService.updateDeviceId(
                    customerByPhoneNumber.getId(),
                    deviceId
            );
        } else {
            if (!customerByDeviceID.getId().equals(customerByPhoneNumber.getId())) {
                customerService.updateDeviceId(
                        customerByPhoneNumber.getId(),
                        deviceId
                );
                customerService.updateDeviceId(
                        customerByDeviceID.getId(),
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
