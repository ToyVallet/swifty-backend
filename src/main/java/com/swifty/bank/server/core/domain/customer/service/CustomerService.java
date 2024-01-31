package com.swifty.bank.server.core.domain.customer.service;

import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.customer.dto.*;
import com.swifty.bank.server.core.domain.customer.dto.*;

public interface CustomerService {
    Customer join(CustomerJoinDto customerJoinDto);
    Customer find(CustomerFindDto uuid);
    Customer findByDeviceID(CustomerFindByDeviceIDDto dto);
    Customer findByPhoneNumberAndNationality(CustomerFindByPhoneNumberAndNationality dto);
    Customer updatePhoneNumberAndNationality(CustomerUpdatePhoneNumberAndNationalityDto dto);
    Customer updateDeviceID(CustomerUpdateDeviceIDDto dto);
    void withdrawCustomer(CustomerFindDto uuid);
}