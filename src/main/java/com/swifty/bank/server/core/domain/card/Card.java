package com.swifty.bank.server.core.domain.card;

import com.swifty.bank.server.core.domain.BaseEntity;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.card.constant.CardStatus;
import com.swifty.bank.server.core.domain.customer.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Entity
@Table(name = "tb_card")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    @Id
    private UUID id;

    private String cardNumber;

    private String name;

    private String nickName;

    @OneToMany(fetch = FetchType.LAZY)
    private Customer customer;

    private String password;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private String cvc;

    @OneToOne(fetch = FetchType.LAZY)
    private UnitedAccount unitedAccount;

    private LocalDate expirationDate;

    public static Card create(String name, String nickName, String cardNumber, Customer customer, String cvc,String password, UnitedAccount unitedAccount){
        return Card.builder()
                .id(UUID.randomUUID())
                .cardNumber(cardNumber)
                .name(name)
                .nickName(nickName)
                .customer(customer)
                .password(password)
                .unitedAccount(unitedAccount)
                .cvc(cvc)
                .status(CardStatus.ACTIVE)
                .build();
    }

    @PrePersist
    public void onPrePersist() {
        this.expirationDate = getCreatedDate().plusYears(5);
    }

}
