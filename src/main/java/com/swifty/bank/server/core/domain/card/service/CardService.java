package com.swifty.bank.server.core.domain.card.service;

import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.card.Card;
import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardService {
    Card createCard(Customer customer, UnitedAccount unitedAccount, String password);

    Optional<Card> getCardByUuid(UUID cardUuid);

    Optional<Card> getCardByCardNumber(String cardNumber);
    List<Card> getCards(Customer customer);

}
