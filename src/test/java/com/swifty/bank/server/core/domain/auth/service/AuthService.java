package com.swifty.bank.server.core.domain.auth.service;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.repository.AuthRepository;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthService {
    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Access-Token 생성")
    public void createAccessTokenTest( ) {
        UUID customerUuid = UUID.randomUUID();

        String accessToken = authenticationService.createAccessToken(customerUuid);
        assertThat(!JwtUtil.isExpiredToken(accessToken));
        assertThat(JwtUtil.getSubject(accessToken)).isEqualTo("AccessToken");
        assertThat(JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class).compareTo(customerUuid) == 0);
    }

    @Test
    @DisplayName("Refresh-Token 생성")
    public void createRefreshTokenTest( ) {
        UUID customerUuid = UUID.randomUUID();

        String refreshToken = authenticationService.createRefreshToken(customerUuid);
        assertThat(!JwtUtil.isExpiredToken(refreshToken));
        assertThat(JwtUtil.getSubject(refreshToken)).isEqualTo("Auth");
        assertThat(JwtUtil.getValueByKeyWithObject(refreshToken, "customerUuid", UUID.class).compareTo(customerUuid) == 0);
    }

    @Test
    @DisplayName("Refresh-Token 생성")
    public void createTemporaryTokenTest( ) {
        String temporaryToken = authenticationService.createTemporaryToken();
        assertThat(JwtUtil.getSubject(temporaryToken)).isEqualTo("TemporaryToken");
    }

    @Test
    @DisplayName("토큰 DTO (RefreshToken + AccessToken) 생성")
    public void generateTokenDtoTest( ) {
        UUID customerUuid = UUID.randomUUID();

        String accessToken = authenticationService.createAccessToken(customerUuid);
        String refreshToken = authenticationService.createRefreshToken(customerUuid);
        TokenDto res = new TokenDto(accessToken, refreshToken);

        when(authenticationService.generateTokenDto(customerUuid))
                .thenReturn(res);
    }

    @Test
    @DisplayName("없는 리프레시 토큰을 DB에 저장하기")
    public void storeNotExistingRefreshTokenTest( ) {
        UUID customerUuid = UUID.randomUUID();

        String refreshToken = authenticationService.createRefreshToken(customerUuid);

        Auth storedAuth = authenticationService.saveRefreshTokenInDatabase(refreshToken);
        assertThat(storedAuth).isInstanceOf(Auth.class);
        assertThat(storedAuth.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(storedAuth.getCustomerUuid()).isEqualTo(customerUuid);
    }

    @Test
    @DisplayName("있는 리프레시 토큰을 DB에서 업데이트하기")
    public void storeExistingRefreshToken( ) {
        UUID customerUuid = UUID.randomUUID();

        String refreshToken = authenticationService.createRefreshToken(customerUuid);

        authenticationService.saveRefreshTokenInDatabase(refreshToken);

        refreshToken = authenticationService.createRefreshToken(customerUuid);
        Auth updatedAuth = authenticationService.saveRefreshTokenInDatabase(refreshToken);

        assertThat(updatedAuth).isInstanceOf(Auth.class);
        assertThat(updatedAuth.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(updatedAuth.getCustomerUuid()).isEqualTo(customerUuid);
    }

    @Test
    @DisplayName("저장되지 않은 리프레시 토큰을 DB에서 조회하기")
    public void findNotExistingAuthTest( ) {
        UUID customerUuid = UUID.randomUUID();

        assertThat(authenticationService.findAuthByCustomerUuid(customerUuid).isEmpty());
    }

    @Test
    @DisplayName("저장된 리프레시 토큰을 DB에서 조회하기")
    public void findExistingAuthTest( ) {
        UUID customerUuid = UUID.randomUUID();
        String refreshToken = authenticationService.createRefreshToken(customerUuid);

        Optional<Auth> maybeAuth = authenticationService.findAuthByCustomerUuid(customerUuid);
        assertThat(maybeAuth.isPresent());
        assertThat(maybeAuth.get().getCustomerUuid().compareTo(customerUuid));
        assertThat(maybeAuth.get().getRefreshToken().compareTo(refreshToken));
    }

    @Test
    @DisplayName("저장된 리프레시 토큰을 삭제하기")
    public void deleteExistingAuthTest( ) {
        UUID customerUuid = UUID.randomUUID();
        String refreshToken = authenticationService.createRefreshToken(customerUuid);

        authenticationService.saveRefreshTokenInDatabase(refreshToken);
        authenticationService.deleteAuth(customerUuid);

        Optional<Auth> maybeAuth = authenticationService.findAuthByCustomerUuid(customerUuid);

        assertThat(maybeAuth.isEmpty());
    }
}
