package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.CustomerAPIService;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerAPIServiceImpl implements CustomerAPIService {
    private final CustomerService customerService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public ResponseResult<?> getCustomerInfo(UUID customerUuid) {

        Optional<CustomerInfoResponse> mayBeCustomerInfo = customerService.findCustomerInfoDtoByUuid(customerUuid);
        if (mayBeCustomerInfo.isEmpty()) return ResponseResult.builder()
                .result(Result.SUCCESS)
                .message("회원정보가 존재하지 않습니다.")
                .build();

        CustomerInfoResponse customerInfoResponse = mayBeCustomerInfo.get();

        return ResponseResult.builder()
                .result(Result.SUCCESS)
                .message("성공적으로 회원정보를 조회하였습니다.")
                .data(customerInfoResponse)
                .build();
    }


    @Override
    public ResponseResult<?> customerInfoUpdate(UUID customerUuid, CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        try {
            Customer customer = customerService.updateCustomerInfo(customerUuid, customerInfoUpdateCondition);

            return ResponseResult.builder()
                    .result(Result.SUCCESS)
                    .message("성공적으로 회원정보를 수정하였습니다.")
                    .data(customer)
                    .build();

        }catch (NoSuchElementException e) {
            return ResponseResult.builder()
                    .result(Result.FAIL)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseResult<?> confirmPassword(UUID customerUuid, String password) {
        try {
            Optional<Customer> mayBeCustomer = customerService.findByUuid(customerUuid);
            if (mayBeCustomer.isEmpty()) return ResponseResult.builder()
                    .result(Result.FAIL)
                    .message("회원이 존재하지 않습니다.")
                    .build();

            Customer customer = mayBeCustomer.get();

            if (encoder.matches(password,customer.getPassword())) return ResponseResult.builder()
                    .result(Result.SUCCESS)
                    .message("비밀번호가 일치합니다.")
                    .build();

            return ResponseResult.builder()
                    .result(Result.FAIL)
                    .message("비밀번호가 일치하지 않습니다.")
                    .build();

        }catch (Exception e) {
            return ResponseResult.builder()
                    .result(Result.FAIL)
                    .message("회원이 존재하지 않습니다.")
                    .data(e.getMessage())
                    .build();
        }
    }

    @Transactional
    @Override
    public ResponseResult<?> resetPassword(UUID customerUuid, String newPassword) {
        try {
            customerService.updatePassword(customerUuid,newPassword);

            return ResponseResult.builder()
                    .result(Result.SUCCESS)
                    .message("성공적으로 비밀번호를 변경하였습니다.")
                    .build();

        }catch (NoSuchElementException e){
            return ResponseResult.builder()
                    .result(Result.FAIL)
                    .message("비밀번호 변경을 실패하였습니다.")
                    .build();
        }
    }

    @Transactional
    @Override
    public ResponseResult<?> customerWithdrawal(UUID customerUuid) {
        try {
            customerService.withdrawCustomer(customerUuid);

            return ResponseResult.builder()
                    .result(Result.SUCCESS)
                    .message("회원탈퇴를 성공적으로 완료하였습니다.")
                    .build();
        }catch (NoSuchElementException e) {
            return ResponseResult.builder()
                    .result(Result.FAIL)
                    .message("회원탈퇴를 실패하였습니다.")
                    .build();
        }

    }
}
