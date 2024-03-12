package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}