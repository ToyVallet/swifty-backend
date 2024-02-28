package com.swifty.bank.server.api.controller.dto.test;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResponse {
    private int your_age;
    private String your_name;
    private String your_phone_number;
}