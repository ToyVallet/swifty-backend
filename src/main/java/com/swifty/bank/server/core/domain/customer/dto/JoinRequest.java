package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class JoinRequest {
    private UUID uuid;
    @NotNull
    @NotBlank
    private String name;
    private Nationality nationality;
    @NotNull
    @Size(max = 11, min = 3)
    @Pattern(regexp = "^\\d+$\n")
    private String phoneNumber;
    @NotBlank
    @NotNull
    @Size(max = 6, min = 6)
    private String password;
    @NotBlank
    @NotNull
    private String deviceId;
    @NotNull
    @Pattern(regexp = "MALE|FEMALE|NONE", message = "[ERROR] gender is neither male nor female")
    private Gender gender;
    @NotNull
    @Pattern(regexp = "^\\d+$\n")
    @Size(max = 8, min = 8)
    private String birthDate;
    private GrantedAuthority roles;
}