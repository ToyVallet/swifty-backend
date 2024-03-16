package com.swifty.bank.server.core.domain.auth.service;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.repository.AuthRepository;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.authentication.service.impl.AuthenticationServiceImpl;
import com.swifty.bank.server.core.utils.JwtUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
@WebAppConfiguration
@SpringBootTest
public class AuthService {
    @SpyBean
    private AuthRepository authRepository;

    @SpyBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Access-Token 생성")
    public void createAccessTokenTest( ) {
        UUID customerUuid = UUID.randomUUID();

        String accessToken = authenticationService.createAccessToken(customerUuid);
        assertThat(!JwtUtil.isExpiredToken(accessToken)).isTrue();
        assertThat(JwtUtil.getSubject(accessToken)).isEqualTo("AccessToken");
        assertThat(JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class).compareTo(customerUuid) == 0).isTrue();
    }

    @Test
    @DisplayName("Refresh-Token 생성")
    public void createRefreshTokenTest( ) {
        UUID customerUuid = UUID.randomUUID();

        String refreshToken = authenticationService.createRefreshToken(customerUuid);
        assertThat(!JwtUtil.isExpiredToken(refreshToken)).isTrue();
        assertThat(JwtUtil.getSubject(refreshToken)).isEqualTo("RefreshToken");
        assertThat(JwtUtil.getValueByKeyWithObject(refreshToken, "customerUuid", UUID.class).compareTo(customerUuid) == 0).isTrue();
    }

    @Test
    @DisplayName("Temporary-Token 생성")
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

        assertThat(res.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(res.getAccessToken()).isEqualTo(accessToken);
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
        authenticationService.saveRefreshTokenInDatabase(refreshToken);

        Optional<Auth> maybeAuth = authenticationService.findAuthByCustomerUuid(customerUuid);
        assertThat(maybeAuth.isPresent()).isTrue();
        assertThat(maybeAuth.get().getCustomerUuid().compareTo(customerUuid) == 0).isTrue();
        assertThat(maybeAuth.get().getRefreshToken().compareTo(refreshToken) == 0).isTrue();
    }

    @Test
    @DisplayName("저장된 리프레시 토큰을 삭제하기")
    public void deleteExistingAuthTest( ) {
        UUID customerUuid = UUID.randomUUID();
        String refreshToken = authenticationService.createRefreshToken(customerUuid);

        authenticationService.saveRefreshTokenInDatabase(refreshToken);
        authenticationService.deleteAuth(customerUuid);

        Optional<Auth> maybeAuth = authenticationService.findAuthByCustomerUuid(customerUuid);

        assertThat(maybeAuth.isEmpty()).isTrue();
    }

    @Test
    public void notRefreshToken( ) {
        UUID customerUuid = UUID.randomUUID();
        String accessToken = authenticationService.createAccessToken(customerUuid);

        assertThat(!authenticationService.isValidateRefreshToken(accessToken)).isTrue();
    }

    @Test
    public void notStoredRefreshToken( ) {
        UUID customerUuid = UUID.randomUUID();
        String accessToken = authenticationService.createAccessToken(customerUuid);

        assertThat(!authenticationService.isValidateRefreshToken(accessToken)).isTrue();
    }

    @Test
    public void validRefreshToken( ) {
        UUID customerUuid = UUID.randomUUID();
        String refreshToken = authenticationService.createRefreshToken(customerUuid);

        authenticationService.saveRefreshTokenInDatabase(refreshToken);

        assertThat(authenticationService.isValidateRefreshToken(refreshToken)).isTrue();
    }

    @Test
    public void shortPassword( ) {
        String password = "0";
        assertThat(!authenticationService.isValidateSignUpPassword(password, "101010", "+821011111111")).isTrue();
    }

    @Test
    public void repeatedPassword( ) {
        String password = "000000";
        assertThat(!authenticationService.isValidateSignUpPassword(password, "101010", "+821011111111")).isTrue();
    }

    @Test
    public void birthdayPassword( ) {
        String password = "101010";
        assertThat(!authenticationService.isValidateSignUpPassword(password, "101010", "+821011111111")).isTrue();
    }

    @Test
    public void phoneNumberPassword( ) {
        String password = "111111";
        assertThat(!authenticationService.isValidateSignUpPassword(password, "101010", "+821011111111")).isTrue();
    }
}
