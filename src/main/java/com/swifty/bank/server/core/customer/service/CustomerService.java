package com.swifty.bank.server.core.customer.service;

import com.swifty.bank.server.core.customer.Customer;
import com.swifty.bank.server.core.customer.dto.CustomerFindDto;
import com.swifty.bank.server.core.customer.dto.CustomerJoinDto;

import java.util.UUID;

public interface CustomerService {
    void join(CustomerJoinDto customerJoinDto);
    Customer find(CustomerFindDto uuid);
    Customer updatePhoneNumber(CustomerJoinDto customerJoinDto);
    void withdrawCustomer(CustomerFindDto uuid);
}