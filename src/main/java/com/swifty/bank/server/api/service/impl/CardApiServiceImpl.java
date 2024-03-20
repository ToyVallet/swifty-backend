package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.card.request.CreateCardRequest;
import com.swifty.bank.server.api.service.CardApiService;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.service.AccountService;
import com.swifty.bank.server.core.domain.card.Card;
import com.swifty.bank.server.core.domain.card.service.CardService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.core.utils.SBoxUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CardApiServiceImpl implements CardApiService {
    private final CardService cardService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final SBoxKeyRedisService sBoxKeyRedisService;

    @Transactional
    @Override
    public void createCard(String accessToken, String keypadToken, CreateCardRequest createCardRequest) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);

        if (maybeCustomer.isEmpty()) throw new NoSuchElementException("회원이 존재하지 않습니다.");
        Customer customer = maybeCustomer.get();

        Optional<UnitedAccount> maybeUnitedAccount = accountService.findOneByUuid(createCardRequest.getUnitedAccountUuid());
        if (maybeUnitedAccount.isEmpty()) throw new NoSuchElementException("계좌가 존재하지 않습니다.");
        UnitedAccount account = maybeUnitedAccount.get();


        List<Integer> key = sBoxKeyRedisService.getData(keypadToken).getKey();
        List<Integer> decrypted = SBoxUtil.decrypt(createCardRequest.getPushedOrder(), key);
        String password = String.join("",
                decrypted
                        .stream()
                        .map(Object::toString)
                        .toList()
        );

        if (password.length() != 4) throw new IllegalArgumentException("비밀번호 양식이 맞지 않습니다.");

        cardService.createCard(customer, account, password);
    }
}
