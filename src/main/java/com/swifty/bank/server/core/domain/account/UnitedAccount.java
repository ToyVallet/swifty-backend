package com.swifty.bank.server.core.domain.account;

import com.swifty.bank.server.core.common.constant.Product;
import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.BaseEntity;
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
public class UnitedAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID unitedAccountUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "FK_customer"))
    private Customer customer;

    private String accountNumber;

    private Product product;

    private String accountPassword;

    private String nickname;

    private Currency defaultCurrency;

    private AccountStatus status;

    @Builder
    public UnitedAccount(
            Customer customer,
            String accountNumber,
            Product product,
            String accountPassword,
            Currency defaultCurrency
    ) {
        this.customer = customer;
        this.accountNumber = accountNumber;
        this.product = product;
        this.accountPassword = accountPassword;
        this.defaultCurrency = defaultCurrency;
        this.nickname = null;
        this.status = AccountStatus.ACTIVE;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }
}
