package com.swifty.bank.server.core.domain.card.service;

import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.card.Card;
import com.swifty.bank.server.core.domain.card.repository.CardRepository;
import com.swifty.bank.server.core.domain.card.service.impl.CardServiceImpl;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Spy
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private CardServiceImpl cardService;


    @Test
    void createCard() {
        JoinDto joinDto = new JoinDto(null, "asdasd", Nationality.KOREA, "01000001111", "23213", "sadasd", Gender.MALE,
                "19950601", UserRole.CUSTOMER);
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name(joinDto.getName())
                .gender(joinDto.getGender())
                .birthDate(joinDto.getBirthDate())
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(joinDto.getNationality())
                .phoneNumber(joinDto.getPhoneNumber())
                .password(encoder.encode(joinDto.getPassword()))
                .deviceId(joinDto.getDeviceId())
                .roles(joinDto.getRoles())
                .build();

        UnitedAccount account = UnitedAccount.builder()
                        .customer(customer)
                        .build();

        Card card = Card.create(
                "account.getNickname()",
                "",
                "123456",
                customer,
                "050",
                "123123",
                account);

        when(cardRepository.save(any(Card.class)))
                .thenReturn(card);

        Card createCard = cardService.createCard(customer, account, "123123");
        assertThat(createCard.getCardNumber()).isEqualTo(card.getCardNumber());
        assertThat(createCard.getCvc()).isEqualTo(card.getCvc());
        assertThat(createCard.getPassword()).isEqualTo(card.getPassword());
        assertThat(createCard.getCustomer().getId()).isEqualTo(card.getCustomer().getId());
    }

    @Test
    void getCardByUuid() {
    }

    @Test
    void getCardByCardNumber() {
    }

    @Test
    void getCards() {
    }
}