package com.swifty.bank.server.src.main.core.customer.service;

import com.swifty.bank.server.src.main.core.customer.dto.CustomerJoinDto;
import java.util.UUID;

public interface CustomerService {
    void join(CustomerJoinDto customerJoinDto);
}