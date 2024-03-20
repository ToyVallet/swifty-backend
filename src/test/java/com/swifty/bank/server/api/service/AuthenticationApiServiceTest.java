package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.ConfigureContainer;
import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.*;
import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenRedisService;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.domain.keypad.service.SecureKeypadService;
import com.swifty.bank.server.core.domain.keypad.service.dto.SecureKeypadDto;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.core.utils.SBoxUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.swifty.bank.server.core.utils.SBoxUtil.encrypt;
import static com.swifty.bank.server.core.utils.SBoxUtil.generateKey;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationApiServiceTest extends ConfigureContainer {
    @Autowired
    private AuthenticationApiService authenticationApiService;
    @Autowired
    private SecureKeypadService secureKeypadService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SBoxKeyRedisService sBoxKeyRedisService;
    @Autowired
    private LogoutAccessTokenRedisService logoutAccessTokenRedisService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static List<Integer> customerPassword = new ArrayList<>(List.of(6, 1, 2, 3, 9, 7));
    private static String temporaryToken = "";
    private static String accessToken = "";
    private static String refreshToken = "";
    private static List<Integer> secureKey = new ArrayList<>();
    private static String secureKeypadToken = "";

    @Test
    @Order(1)
    public void checkLoginAvailabilityWithNonExistCustomer() {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("John Doe")
                .phoneNumber("+821098765432")
                .mobileCarrier("KT")
                .residentRegistrationNumber("9901011")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);

        assertThat(res.getIsAvailable()).isTrue();
        assertThat(!res.getTemporaryToken().isEmpty()).isTrue();
    }

    @Test
    @Order(2)
    public void checkLoginAvailabilityWithExistValidCustomer() {
        JoinDto joinDto = new JoinDto(null, "Taylor Swift", Nationality.KOREA, "+821012345678",
                customerPassword.stream()
                        .map((p) -> p.toString())
                        .collect(Collectors.joining("")),
                "iPhone", Gender.FEMALE,
                "010101", UserRole.CUSTOMER);
        customerService.join(joinDto);

        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
        temporaryToken = res.getTemporaryToken();

        assertThat(res.getIsAvailable()).isTrue();
        assertThat(!res.getTemporaryToken().isEmpty()).isTrue();
    }

    @Test
    @Order(3)
    public void createSecureKeypad( ) {
        CreateSecureKeypadResponse res = authenticationApiService.createSecureKeypad();

        assertThat(res.getKeypad() != null).isTrue();
        assertThat(res.getKeypadToken() != null).isTrue();

        SBoxKey key = sBoxKeyRedisService.getData(res.getKeypadToken());
        secureKeypadToken = res.getKeypadToken();
        secureKey = key.getKey();

        assertThat(key != null).isTrue();
        assertThat(key.getKey() != null).isTrue();
    }

    @Test
    @Order(4)
    public void signUpAndSignInWithNotExistTemporaryToken() {
        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(SBoxUtil.encrypt(customerPassword, secureKey))
                .build();

        assertThatThrownBy(() -> authenticationApiService.signUpAndSignIn("", "", reqForSign))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @Order(4)
    public void signUpAndSignInWithNotValidPassword() {

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encrypt(List.of(1, 1, 1, 1, 1, 1), secureKey))
                .build();

        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(temporaryToken, secureKeypadToken, reqForSign);
        assertThat(!res.isSuccess()).isTrue();
        assertThat(!res.isAvailablePassword()).isTrue();
        assertThat(res.getTokens() == null).isTrue();
    }

    @Test
    @Order(5)
    public void signUpAndSignInWithValidNewInfo() {

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encrypt(customerPassword, secureKey))
                .build();

        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(temporaryToken, secureKeypadToken, reqForSign);
        assertThat(res.isAvailablePassword()).isTrue();
        assertThat(res.isSuccess()).isTrue();
        assertThat(!res.getTokens().isEmpty()).isTrue();
    }

    @Test
    @Order(6)
    public void signUpAndSignInWithNotMatchPassword() {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse resForCheck = authenticationApiService.checkLoginAvailability(req);
        temporaryToken = resForCheck.getTemporaryToken();
        secureKeypadToken = authenticationApiService.createSecureKeypad().getKeypadToken();
        secureKey = sBoxKeyRedisService.getData(secureKeypadToken).getKey();

        customerPassword = List.of(6, 9, 3, 5, 6, 7);

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encrypt(customerPassword, secureKey))
                .build();

        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(temporaryToken, secureKeypadToken, reqForSign);

        assertThat(res.isSuccess()).isTrue();
        assertThat(res.isAvailablePassword()).isTrue();
        assertThat(res.getTokens() == null).isFalse();
    }

    @Test
    @Order(9)
    public void signUpAndSignInWithDifferentDeviceId() {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse resForCheck = authenticationApiService.checkLoginAvailability(req);
        temporaryToken = resForCheck.getTemporaryToken();
        secureKeypadToken = authenticationApiService.createSecureKeypad().getKeypadToken();
        secureKey = sBoxKeyRedisService.getData(secureKeypadToken).getKey();

        customerPassword = List.of(8, 2, 9, 4, 0, 1);

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("Android")
                .pushedOrder(encrypt(customerPassword, secureKey))
                .build();

        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, secureKeypadToken, reqForSign);

        accessToken = resForSignIn.getTokens().get(0);
        refreshToken = resForSignIn.getTokens().get(1);
        assertThat(resForSignIn.isSuccess()).isTrue();
        assertThat(resForSignIn.isAvailablePassword()).isTrue();
        assertThat(resForSignIn.getTokens().isEmpty()).isFalse();
        assertThat(customerService.findByPhoneNumber("+821012345678").isPresent()).isTrue();
        Customer customer = customerService.findByPhoneNumber("+821012345678").get();
        assertThat(customer.getDeviceId().equals("Android")).isTrue();
        assertThat(customer.getName().equals("Taylor Swift")).isTrue();
        assertThat(encoder.matches(customerPassword.stream()
                .map((p) -> p.toString())
                .collect(Collectors.joining()), customer.getPassword())).isTrue();
    }

    @Test
    @Order(10)
    public void reissueWithNotValidRefreshToken() {
        String notValidRefreshToken = authenticationService.createRefreshToken(UUID.randomUUID());

        assertThat(!authenticationApiService.reissue(notValidRefreshToken).getIsSuccess()).isTrue();
    }

    @Test
    @Order(11)
    public void reissueTest() {
        ReissueResponse res = authenticationApiService.reissue(refreshToken);

        assertThat(res.getIsSuccess());
        assertThat(!res.getTokens().isEmpty());
        accessToken = res.getTokens().get(0);
        refreshToken = res.getTokens().get(1);
    }

    @Test
    @Order(12)
    public void logoutTest() {
        LogoutResponse res = authenticationApiService.logout(accessToken);

        assertThat(res.getIsSuccess()).isTrue();
        assertThat(logoutAccessTokenRedisService.getData(accessToken).equals("false"));
    }

    @Test
    @Order(14)
    public void loginAfterLogOutTest() {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
        temporaryToken = res.getTemporaryToken();
        secureKeypadToken = authenticationApiService.createSecureKeypad().getKeypadToken();
        secureKey = sBoxKeyRedisService.getData(secureKeypadToken).getKey();
        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("Android")
                .pushedOrder(encrypt(customerPassword, secureKey))
                .build();

        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, secureKeypadToken, reqForSign);

        assertThat(resForSignIn.isSuccess());
        assertThat(resForSignIn.isAvailablePassword());
        assertThat(!resForSignIn.getTokens().isEmpty());
        accessToken = resForSignIn.getTokens().get(0);
        refreshToken = resForSignIn.getTokens().get(1);
    }

    @Test
    @Order(15)
    public void signOutTest() {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        SignOutResponse res = authenticationApiService.signOut(accessToken);

        assertThat(res.getIsSuccess()).isTrue();
        assertThat(logoutAccessTokenRedisService.getData(accessToken).equals("false"));
        assertThat(authenticationService.findAuthByCustomerUuid(customerUuid).isEmpty());
        assertThat(customerService.findByUuid(customerUuid).isEmpty());
    }

    @Test
    @Order(17)
    public void signUpTestAfterSignOut() {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
        temporaryToken = res.getTemporaryToken();
        secureKeypadToken = authenticationApiService.createSecureKeypad().getKeypadToken();
        secureKey = sBoxKeyRedisService.getData(secureKeypadToken).getKey();

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("Android")
                .pushedOrder(encrypt(customerPassword, secureKey))
                .build();

        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, secureKeypadToken, reqForSign);

        assertThat(resForSignIn.isSuccess());
        assertThat(resForSignIn.isAvailablePassword());
        assertThat(!resForSignIn.getTokens().isEmpty());
    }
}
