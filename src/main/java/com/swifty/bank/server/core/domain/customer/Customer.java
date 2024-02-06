package com.swifty.bank.server.core.domain.customer;

import com.swifty.bank.server.core.domain.BaseEntity;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

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

    private String deviceId;

    private String name;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String birthDate;

    @Enumerated(EnumType.STRING)
    private Nationality nationality;

    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;  // 휴면 상태, 정지된 사용자 등

    private String password;

    private GrantedAuthority roles;

    @Builder
    public Customer(UUID id, String deviceId, String name, String phoneNumber, Gender gender, String birthDate, Nationality nationality, CustomerStatus customerStatus, String password, GrantedAuthority roles) {
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.customerStatus = customerStatus;
        this.password = password;
        this.roles = roles;
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

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateBirthDate(String newBirthDate) {
        this.birthDate = newBirthDate;
    }

    public void resetPassword(String newPassword) {
        this.password = newPassword;
    }

}