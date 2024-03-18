package com.swifty.bank.server.core.domain.card.repository.jpqlrepository.impl;


import com.swifty.bank.server.core.domain.card.Card;
import com.swifty.bank.server.core.domain.card.repository.jpqlrepository.CardJpqlRepository;
import com.swifty.bank.server.core.domain.customer.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CardJpqlRepositoryImpl implements CardJpqlRepository {
    private final EntityManager em;
    private final boolean isDeleted = false;

    @Override
    public Long getCardCount(String cardNumber) {
        return em.createQuery("SELECT COUNT(c) " +
                        "FROM Card c " +
                        "WHERE c.cardNumber = :cardNumber", Long.class)
                .setParameter("cardNumber", cardNumber)
                .getSingleResult();
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) {
        try {
            Card card = em.createQuery("SELECT c " +
                            "FROM Card c " +
                            "WHERE c.cardNumber = :cardNumber", Card.class)
                    .setParameter("cardNumber", cardNumber)
                    .getSingleResult();
            return Optional.of(card);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Card> getCards(Customer customer) {
        return em.createQuery("SELECT c " +
                        "FROM Card c " +
                        "WHERE c.customer = :customer", Card.class)
                .setParameter("customer", customer)
                .getResultList();
    }
}
