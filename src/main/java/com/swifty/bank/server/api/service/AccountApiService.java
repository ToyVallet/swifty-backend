package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.account.request.AccountRegisterRequest;
import com.swifty.bank.server.api.controller.dto.account.request.RetrieveBalanceWithCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountNicknameRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseUnitedAccountPasswordRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateDefaultCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateSubAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateUnitedAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.WithdrawUnitedAccountRequest;
import com.swifty.bank.server.api.controller.dto.account.response.AccountRegisterResponse;
import com.swifty.bank.server.api.controller.dto.account.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.controller.dto.account.response.ListUnitedAccountWithCustomerResponse;
import com.swifty.bank.server.api.controller.dto.account.response.RetrieveBalanceWithCurrencyResponse;
import com.swifty.bank.server.api.controller.dto.account.response.ReviseUnitedAccountPasswordResponse;
import com.swifty.bank.server.api.controller.dto.account.response.UpdateAccountNicknameResponse;
import com.swifty.bank.server.api.controller.dto.account.response.UpdateDefaultCurrencyResponse;
import com.swifty.bank.server.api.controller.dto.account.response.UpdateSubAccountStatusResponse;
import com.swifty.bank.server.api.controller.dto.account.response.UpdateUnitedAccountStatusResponse;
import com.swifty.bank.server.api.controller.dto.account.response.WithdrawUnitedAccountResponse;

public interface AccountApiService {
    AccountRegisterResponse register(String accessToken, AccountRegisterRequest req);

    UpdateAccountNicknameResponse updateNickname(String accessToken, ReviseAccountNicknameRequest req);

    ReviseUnitedAccountPasswordResponse updatePassword(String accessToken, ReviseUnitedAccountPasswordRequest req);

    RetrieveBalanceWithCurrencyResponse retrieveBalanceWithCurrency(String accessToken,
                                                                    RetrieveBalanceWithCurrencyRequest req);

    WithdrawUnitedAccountResponse withdraw(String accessToken, WithdrawUnitedAccountRequest req);

    UpdateUnitedAccountStatusResponse updateUnitedAccountStatus(String accessToken,
                                                                UpdateUnitedAccountStatusRequest req);

    UpdateSubAccountStatusResponse updateSubAccountStatus(String accessToken, UpdateSubAccountStatusRequest req);

    UpdateDefaultCurrencyResponse updateDefaultCurrency(String accessToken, UpdateDefaultCurrencyRequest req);

    ListUnitedAccountWithCustomerResponse listUnitedAccountWithCustomer(String accessToken);

    CreateSecureKeypadResponse createSecureKeypad(String accessToken);
}
