package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import lombok.Getter;

@Getter
public class CustomerInfoUpdateConditionRequest {

    private String name;

    private String phoneNumber;

    private String birthDate;

    private Nationality nationality;

    public CustomerInfoUpdateConditionRequest(String name, String phoneNumber, String birthDate, Nationality nationality) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }
}
