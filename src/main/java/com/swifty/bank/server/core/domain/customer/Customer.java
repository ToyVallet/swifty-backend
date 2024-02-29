package com.swifty.bank.server.core.domain.customer;

import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.domain.BaseEntity;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.exception.common.NonExistOrOverOneResultException;
import jakarta.persistence.*;
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

import java.util.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    private UserRole roles;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UnitedAccount> unitedAccounts;

    @Builder
    public Customer(UUID id, String deviceId, String name, String phoneNumber, Gender gender, String birthDate, Nationality nationality, CustomerStatus customerStatus, String password, GrantedAuthority roles, UserRole userRole,
                    List<UnitedAccount> unitedAccounts) {
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.customerStatus = customerStatus;
        this.password = password;
        this.roles = userRole;
        this.unitedAccounts = new ArrayList<>( );
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

    public void addUnitedAccount(UnitedAccount ua) {
        this.unitedAccounts.add(ua);
    }

    public UnitedAccount findUnitedAccountByUnitedAccountId(UUID unitedAccountId) {
        List<UnitedAccount> unitedAccounts =  this.unitedAccounts.stream()
                .filter(unitedAccount -> {
                    return unitedAccountId.compareTo(unitedAccount.getUnitedAccountUuid()) == 0;
                }).toList();

        if (unitedAccounts.size() == 1) {
            return unitedAccounts.get(0);
        }

        throw new NonExistOrOverOneResultException( );
    }

    public void removeUnitedAccountByUnitedAccountId(UUID unitedAccountId) {
        UnitedAccount unitedAccount = findUnitedAccountByUnitedAccountId(unitedAccountId);
        unitedAccount.delete();
    }
}