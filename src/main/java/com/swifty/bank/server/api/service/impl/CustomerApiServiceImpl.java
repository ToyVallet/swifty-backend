package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.domain.keypad.service.SecureKeypadService;
import com.swifty.bank.server.core.domain.keypad.service.dto.SecureKeypadDto;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.core.utils.SBoxUtil;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerApiServiceImpl implements CustomerApiService {
    private final CustomerService customerService;
    private final SecureKeypadService secureKeypadService;

    private final SBoxKeyRedisService sBoxKeyRedisService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public CustomerInfoResponse getCustomerInfo(String accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        CustomerInfoDto customerInfoDto = customerService.findCustomerInfoDtoByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException("고객 조회에 실패했습니다."));

        return CustomerInfoResponse.builder()
                .name(customerInfoDto.getName())
                .phoneNumber(customerInfoDto.getPhoneNumber())
                .gender(customerInfoDto.getGender())
                .birthDate(customerInfoDto.getBirthDate())
                .nationality(customerInfoDto.getNationality())
                .customerStatus(customerInfoDto.getCustomerStatus())
                .build();
    }

    @Override
    public void customerInfoUpdate(String accessToken,
                                   CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        customerService.updateCustomerInfo(customerUuid, customerInfoUpdateCondition);

    }

    @Override
    public boolean confirmPassword(String accessToken, PasswordRequest passwordRequest) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        Customer customer = customerService.findByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException("회원 조회에 실패했습니다."));

        String password = decryptPassword(accessToken, passwordRequest.getPushedOrder());

        return encoder.matches(password, customer.getPassword());
    }

    @Override
    public void resetPassword(String accessToken, PasswordRequest passwordRequest) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        String newPassword = decryptPassword(accessToken, passwordRequest.getPushedOrder());

        customerService.updatePassword(customerUuid, newPassword);
    }

    @Override
    public void customerWithdrawal(String accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        customerService.withdrawCustomer(customerUuid);
    }

    @Override
    public CreateSecureKeypadResponse createSecureKeypad(String accessToken) {
        SecureKeypadDto secureKeypadDto = secureKeypadService.createSecureKeypad();

        // redis에 섞은 순서에 대한 정보 저장
        sBoxKeyRedisService.setData(
                accessToken,
                SBoxKey.builder()
                        .key(secureKeypadDto.getKey())
                        .build()
        );

        return CreateSecureKeypadResponse.builder()
                .keypad(secureKeypadDto.getShuffledKeypadImages())
                .build();
    }

    private String decryptPassword(String accessToken, List<Integer> pushedOrder) {
        // 비밀번호 복호화
        List<Integer> key = sBoxKeyRedisService.getData(accessToken).getKey();
        List<Integer> decrypted = SBoxUtil.decrypt(pushedOrder, key);
        return String.join("",
                decrypted
                        .stream()
                        .map(Object::toString)
                        .toList()
        );
    }
}