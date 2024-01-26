package com.swifty.bank.server.core.customer;

import com.swifty.bank.server.core.common.BaseEntity;
import com.swifty.bank.server.core.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.customer.constant.Nationality;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "tb_customer")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class Customer extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    @Id
    private UUID id;    // PK

    private String name;
    private String socialToken;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Nationality nationality;
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;  // 휴면 상태, 정지된 사용자 등

    @Builder
    public Customer(UUID id, String name, String socialToken, String phoneNumber, Nationality nationality,
                    CustomerStatus customerStatus) {
        this.id = id;
        this.name = name;
        this.socialToken = socialToken;
        this.phoneNumber = phoneNumber;
        this.nationality = nationality;
        this.customerStatus = customerStatus;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}