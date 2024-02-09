package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CustomerInfoResponse {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Size(max = 11, min = 3)
    @Pattern(regexp = "^\\d+$\n")
    private String phoneNumber;

    @NotNull
    @Pattern(regexp = "MALE|FEMALE|NONE", message = "[ERROR] gender is neither male nor female")
    private Gender gender;

    @NotNull
    @Pattern(regexp = "^\\d+$\n")
    @Size(max = 8, min = 8)
    private String birthDate;

    @NotNull
    @Pattern(regexp = "KOREA", message = "[ERROR] Customer whose nationality is not supported")
    private Nationality nationality;

    @NotNull
    @Pattern(regexp = "ACTIVE|SUSPENDED|WITHDRAWL", message = "[ERROR] Invalid Customer Status")
    private CustomerStatus customerStatus;

    public CustomerInfoResponse(String name, String phoneNumber, Gender gender, String birthDate, Nationality nationality, CustomerStatus customerStatus) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.customerStatus = customerStatus;
    }
}
