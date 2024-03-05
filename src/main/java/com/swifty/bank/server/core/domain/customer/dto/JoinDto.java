package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JoinDto {
    private UUID uuid;
    private String name;
    private Nationality nationality;
    private String phoneNumber;
    private String password;
    private String deviceId;
    private Gender gender;
    private String birthDate;
    private UserRole roles;

    public static JoinDto changeToJoinDto(TemporarySignUpForm temporarySignUpForm, SignWithFormRequest req) {
        Gender gender = Gender.MALE; // Default gender
        if (temporarySignUpForm.getResidentRegistrationNumber().endsWith("4") ||
                temporarySignUpForm.getResidentRegistrationNumber().endsWith("2")) {
            gender = Gender.FEMALE;
        }

        return JoinDto.builder()
                .name(temporarySignUpForm.getName())
                .phoneNumber(temporarySignUpForm.getPhoneNumber())
                .birthDate(temporarySignUpForm.getResidentRegistrationNumber().substring(0, 6))
                .gender(gender) // Use the determined gender
                .password(req.getPassword())
                .deviceId(req.getDeviceId())
                // You might want to set other properties like `uuid` and `roles` if necessary
                .build();
    }

}