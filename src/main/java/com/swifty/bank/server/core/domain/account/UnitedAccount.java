package com.swifty.bank.server.core.domain.account;

import com.swifty.bank.server.core.common.constant.Product;
import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.BaseEntity;
import com.swifty.bank.server.core.domain.account.constant.AccountStatus;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.exception.common.NonExistOrOverOneResultException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

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

    @OneToMany(mappedBy = "unitedAccount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubAccount> subAccounts;

    @Builder
    public UnitedAccount(
            Customer customer,
            String accountNumber,
            Product product,
            String accountPassword,
            Currency defaultCurrency,
            List<SubAccount> subAccounts
    ) {
        this.customer = customer;
        this.accountNumber = accountNumber;
        this.product = product;
        this.accountPassword = accountPassword;
        this.defaultCurrency = defaultCurrency;
        this.nickname = null;
        this.status = AccountStatus.ACTIVE;
        this.subAccounts = new ArrayList<>( );
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public void updateStatus(AccountStatus status) {
        this.status = status;
    }

    public void addSubAccount(SubAccount subAccount) {
        this.subAccounts.add(subAccount);
    }

    public SubAccount findSubAccountByCurrency(Currency currency) {
        List<SubAccount> subAccounts = this.subAccounts
                .stream()
                .filter(subAccount -> {
                    return currency.equals(subAccount.getCurrency( ));
                })
                .toList();

        if (subAccounts.size( ) == 1) {
            return subAccounts.get(0);
        }

        throw new NonExistOrOverOneResultException();
    }

    public void updateDefaultCurrency(Currency currency) {
        this.defaultCurrency = currency;
    }
}
