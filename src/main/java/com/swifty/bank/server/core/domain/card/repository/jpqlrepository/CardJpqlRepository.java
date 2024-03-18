package com.swifty.bank.server.core.domain.card.repository.jpqlrepository;

import com.swifty.bank.server.core.domain.card.Card;
import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.List;
import java.util.Optional;

public interface CardJpqlRepository {

    Long getCardCount(String cardNumber);
    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> getCards(Customer customer);
}
