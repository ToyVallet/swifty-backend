package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.api.controller.dto.auth.request.JoinRequest;
import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

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

    public static JoinDto createJoinDto(JoinRequest joinRequest) {
        return new JoinDto(
                joinRequest.getUuid(),
                joinRequest.getName(),
                joinRequest.getNationality(),
                joinRequest.getPhoneNumber(),
                joinRequest.getPassword(),
                joinRequest.getDeviceId(),
                joinRequest.getGender(),
                joinRequest.getBirthDate(),
                joinRequest.getRoles()
        );
    }
}