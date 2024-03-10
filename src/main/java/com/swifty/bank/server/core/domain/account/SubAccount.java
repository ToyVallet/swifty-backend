package com.swifty.bank.server.core.domain.account;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.BaseEntity;
import com.swifty.bank.server.core.domain.account.constant.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_sub_account")
public class SubAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID subAccountUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unitedAccountUuid", foreignKey = @ForeignKey(name = "FK_united_account"))
    private UnitedAccount unitedAccount;

    private Currency currency;

    private double balance;

    private AccountStatus status;

    @Builder
    public SubAccount(UnitedAccount ua, Currency cur) {
        this.unitedAccount = ua;
        this.currency = cur;
        this.balance = 0;
        this.status = AccountStatus.ACTIVE;
    }

    public void updateSubAccountStatus(AccountStatus status) {
        this.status = status;
    }
}
