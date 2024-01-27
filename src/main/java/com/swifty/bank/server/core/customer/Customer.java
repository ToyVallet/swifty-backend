package com.swifty.bank.server.core.customer;

import com.swifty.bank.server.core.common.BaseEntity;
import com.swifty.bank.server.core.config.constant.UserRole;
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

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
    private String bod;
    private String sex;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Nationality nationality;
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;  // 휴면 상태, 정지된 사용자 등
    private String deviceId;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public Customer(UUID id, String name, String bod, String sex, String phoneNumber, Nationality nationality,
                    CustomerStatus customerStatus, String deviceId, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.bod = bod;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.nationality = nationality;
        this.customerStatus = customerStatus;
        this.deviceId = deviceId;
        this.password = password;
        this.role = role;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    // for commit
}