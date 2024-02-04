package com.swifty.bank.server.core.domain.customer;

import com.swifty.bank.server.core.domain.BaseEntity;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
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
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
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
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Nationality nationality;
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;  // 휴면 상태, 정지된 사용자 등
    private String password;
    private GrantedAuthority roles;
    private String deviceId;

    @Builder
    public Customer(UUID id, String name, String phoneNumber, Nationality nationality,
                    CustomerStatus customerStatus, String password, String deviceId) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.nationality = nationality;
        this.customerStatus = customerStatus;
        this.password = password;
        this.roles = new SimpleGrantedAuthority("CUSTOMER");
        this.deviceId = deviceId;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public void updateDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}