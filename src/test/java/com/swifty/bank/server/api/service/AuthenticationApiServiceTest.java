package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.ConfigureContainer;
import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.*;
import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenRedisService;
import com.swifty.bank.server.core.common.redis.service.SecureKeypadOrderInverseRedisService;
import com.swifty.bank.server.core.common.redis.service.TemporarySignUpFormRedisService;
import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AuthenticationApiServiceTest extends ConfigureContainer {
    @Autowired
    private AuthenticationApiService authenticationApiService;

    @Autowired
    private static CustomerService customerService;
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SecureKeypadOrderInverseRedisService secureKeypadOrderInverseRedisService;
    @Autowired
    private TemporarySignUpFormRedisService temporarySignUpFormRedisService;
    @Autowired
    private LogoutAccessTokenRedisService logoutAccessTokenRedisService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static String customerPassword = "987654";
    private String temporaryToken = "";
    private String accessToken = "";
    private String refreshToken = "";

    @BeforeAll
    public static void initCustomer( ) {
        JoinDto joinDto = new JoinDto(null, "Taylor Swift", Nationality.KOREA, "+821011111111",
                customerPassword, "iPhone", Gender.FEMALE,
                "990101", UserRole.CUSTOMER);

        customerService.join(joinDto);
    }

    @Test
    public void checkLoginAvailabilityWithNonExistCustomer( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("John Doe")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);

        assertThat(res.getIsAvailable());
        assertThat(!res.getTemporaryToken().isEmpty());
    }

    @Test
    public void checkLoginAvailabilityWithExistNotValidCustomer( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821011111111")
                .mobileCarrier("KT")
                .residentRegistrationNumber("990101")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);

        assertThat(res.getIsAvailable());
        assertThat(!res.getTemporaryToken().isEmpty());
    }

    @Test
    public void checkLoginAvailabilityWithExistValidCustomer( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821011111111")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
        temporaryToken = res.getTemporaryToken();

        assertThat(res.getIsAvailable());
        assertThat(!res.getTemporaryToken().isEmpty());
    }

    @Test
    public void signUpAndSignInWithNotExistTemporaryToken( ) {
        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encryptPassword(temporaryToken, customerPassword))
                .build();

        assertThatThrownBy(() -> authenticationApiService.signUpAndSignIn("", reqForSign))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void signUpAndSignInWithNotValidPassword( ) {

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encryptPassword(temporaryToken, "111111"))
                .build();

        assertThatThrownBy(() -> authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void signUpAndSignInWithNotMatchPassword( ) {

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encryptPassword(temporaryToken, "381943"))
                .build();

        assertThatThrownBy(() -> authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void signUpAndSignInWithValidNewInfo( ) {

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encryptPassword(temporaryToken, customerPassword))
                .build();

        assertThat(authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign).isAvailablePassword());
        assertThat(authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign).isSuccess());
        assertThat(!authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign).getTokens().isEmpty());
        assertThatThrownBy(() -> temporarySignUpFormRedisService.deleteData(temporaryToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void signUpAndSignInWithPresentCustomer( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Ariana Grande")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0303034")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encryptPassword(res.getTemporaryToken(), customerPassword))
                .build();

        assertThat(!authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign).isSuccess());
        assertThat(!authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign).isAvailablePassword());
    }

    @Test
    public void signUpAndSignInWithNotEqualCustomer( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Ariana Grande")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0303034")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("iPhone")
                .pushedOrder(encryptPassword(res.getTemporaryToken(), customerPassword))
                .build();

        assertThat(!authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign).isSuccess());
        assertThat(!authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign).isAvailablePassword());
    }

    @Test
    public void signUpAndSignInWithDifferentDeviceId( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
        customerPassword = "829401";

        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("Android")
                .pushedOrder(encryptPassword(res.getTemporaryToken(), customerPassword))
                .build();

        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);

        assertThat(resForSignIn.isSuccess());
        assertThat(resForSignIn.isAvailablePassword());
        assertThat(resForSignIn.getTokens().isEmpty());
        accessToken = resForSignIn.getTokens().get(0);
        refreshToken = resForSignIn.getTokens().get(1);
        assertThat(customerService.findByPhoneNumber("+821012345678").isPresent());
        assertThat(customerService.findByPhoneNumber("+821012345678").get().getDeviceId().equals("Android"));
        assertThat(customerService.findByPhoneNumber("+821012345678").get().getName().equals("Taylor Swift"));
        assertThat(encoder.matches(customerPassword, customerService.findByPhoneNumber("+821012345678").get().getPassword()));
    }

    @Test
    public void reissueWithNotValidRefreshToken( ) {
        String notValidRefreshToken = authenticationService.createRefreshToken(UUID.randomUUID());

        assertThat(!authenticationApiService.reissue(notValidRefreshToken).getIsSuccess());
    }

    @Test
    public void reissueTest( ) {
        ReissueResponse res = authenticationApiService.reissue(refreshToken);

        assertThat(res.getIsSuccess());
        assertThat(!res.getTokens().isEmpty());
        accessToken = res.getTokens().get(0);
        refreshToken = res.getTokens().get(1);
    }

    @Test
    public void logoutTest( ) {
        LogoutResponse res = authenticationApiService.logout(accessToken);

        assertThat(res.getIsSuccessful());
        assertThat(logoutAccessTokenRedisService.getData(accessToken).equals("false"));
    }

    @Test
    public void useLoggedOutAccessTokenTest( ) {
        LogoutResponse res = authenticationApiService.logout(accessToken);

        assertThatThrownBy(() -> res.getIsSuccessful())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void loginAfterLogOutTest( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("Android")
                .pushedOrder(encryptPassword(res.getTemporaryToken(), customerPassword))
                .build();

        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);

        assertThat(resForSignIn.isSuccess());
        assertThat(resForSignIn.isAvailablePassword());
        assertThat(!resForSignIn.getTokens().isEmpty());
        accessToken = resForSignIn.getTokens().get(0);
        refreshToken = resForSignIn.getTokens().get(1);
    }

    @Test
    public void signOutTest( ) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        SignOutResponse res = authenticationApiService.signOut(accessToken);

        assertThat(res.getWasSignedOut());
        assertThat(logoutAccessTokenRedisService.getData(accessToken).equals("false"));
        assertThat(authenticationService.findAuthByCustomerUuid(customerUuid).isEmpty());
        assertThat(customerService.findByUuid(customerUuid).isEmpty());
    }

    @Test
    public void overlappedSignOutTest( ) {
        assertThatThrownBy(() -> authenticationApiService.signOut(accessToken))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void signUpTestAfterSignOut( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
                .deviceId("Android")
                .pushedOrder(encryptPassword(res.getTemporaryToken(), customerPassword))
                .build();

        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);

        assertThat(resForSignIn.isSuccess());
        assertThat(resForSignIn.isAvailablePassword());
        assertThat(!resForSignIn.getTokens().isEmpty());
    }

    private List<Integer> encryptPassword(String temporaryToken, String password) {
        List<Integer> ans = new ArrayList<>();
        List<Integer> secureKeypadOrderInverse
                = secureKeypadOrderInverseRedisService.getData(temporaryToken)
                .getKeypadOrderInverse();

        for (int i = 0 ; i<6 ; i++) {
            int pos = password.charAt(i) - '0';
            for (int j = 0 ; j<secureKeypadOrderInverse.size() ; j++) {
                int key = secureKeypadOrderInverse.get(i);
                if (key == pos) {
                    ans.add(secureKeypadOrderInverse.get(j));
                    break;
                }
            }
        }

        return ans;
    }
}
