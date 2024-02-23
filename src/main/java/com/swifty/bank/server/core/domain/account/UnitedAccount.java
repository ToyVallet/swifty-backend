package com.swifty.bank.server.core.domain.account;

import com.swifty.bank.server.core.common.constant.Bank;
import com.swifty.bank.server.core.domain.account.constant.AccountStatus;
import com.swifty.bank.server.core.domain.customer.Customer;
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
public class UnitedAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID unitedAccountUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "FK_customer"))
    private Customer customer;

    private String accountNumber;

    private Bank bank;

    private String accountPassword;

    private String nickname;

    private AccountStatus status;

    @Builder
    public UnitedAccount(Customer customer, String accountNumber, Bank bank, String accountPassword) {
        this.customer = customer;
        this.accountNumber = accountNumber;
        this.bank = bank;
        this.accountPassword = accountPassword;
        this.nickname = null;
        this.status = AccountStatus.ACTIVE;
    }
}
