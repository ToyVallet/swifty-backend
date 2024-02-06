package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public class CustomerInfoResponse {

    private String name;

    private String phoneNumber;

    private Gender gender;

    private String birthDate;

    private Nationality nationality;

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
