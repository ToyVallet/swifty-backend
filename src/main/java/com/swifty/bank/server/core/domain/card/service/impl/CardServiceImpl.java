package com.swifty.bank.server.core.domain.card.service.impl;

import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.card.Card;
import com.swifty.bank.server.core.domain.card.repository.CardRepository;
import com.swifty.bank.server.core.domain.card.service.CardService;
import com.swifty.bank.server.core.domain.customer.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final BCryptPasswordEncoder encoder;
    @Override
    public Card createCard(Customer customer, UnitedAccount unitedAccount, String password) {

        Card card = Card.create(
                unitedAccount.getNickname(),
                "",
                createCardNumber(),
                customer,
                createCvcNumber(),
                encoder.encode(password),
                unitedAccount);

        Card saveCard = cardRepository.save(card);
        return saveCard;
    }

    @Override
    public Optional<Card> getCardByUuid(UUID cardUuid) {
        return cardRepository.findById(cardUuid);
    }

    @Override
    public Optional<Card> getCardByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    @Override
    public List<Card> getCards(Customer customer) {
        return cardRepository.getCards(customer);
    }

    private String createCardNumber() {
        Random random = new Random(System.currentTimeMillis());
        String carNum = String.valueOf(random.nextLong(1000000000000000L, 9999999999999999L));
        Long cardCount = cardRepository.getCardCount(carNum);

        while (cardCount != 0) {
            carNum = String.valueOf(random.nextLong(1000000000000000L, 9999999999999999L));
            cardCount = cardRepository.getCardCount(carNum);
        }

        return carNum;
    }

    private String createCvcNumber() {
        Random random = new Random(System.currentTimeMillis());
        String first = String.valueOf(random.nextInt(10));
        random.setSeed(System.currentTimeMillis());
        String second = String.valueOf(random.nextInt(10));
        random.setSeed(System.currentTimeMillis());
        String third = String.valueOf(random.nextInt(10));

        return first+second+third;
    }
}
