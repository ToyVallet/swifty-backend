package com.swifty.bank.server.core.domain.card.repository;

import com.swifty.bank.server.core.domain.card.Card;
import com.swifty.bank.server.core.domain.card.repository.jpqlrepository.CardJpqlRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID>, CardJpqlRepository {

}

