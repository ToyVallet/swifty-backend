package com.swifty.bank.server.core.domain.customer;

import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.domain.BaseEntity;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "tb_customer")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
        List<UnitedAccount> unitedAccounts = this.unitedAccounts.stream()
                .filter(unitedAccount -> unitedAccountId.compareTo(unitedAccount.getUnitedAccountUuid()) == 0).toList();

        if (unitedAccounts.size() == 1) {
            return unitedAccounts.get(0);
        }
        if (unitedAccounts.size() == 0) {
            throw new NoResultException("조회 결과가 없습니다");
        }

        throw new NonUniqueResultException("고유한 값이 아닙니다");
    }

    public void removeUnitedAccountByUnitedAccountId(UUID unitedAccountId) {
        UnitedAccount unitedAccount = findUnitedAccountByUnitedAccountId(unitedAccountId);
        unitedAccount.delete();
    }
}