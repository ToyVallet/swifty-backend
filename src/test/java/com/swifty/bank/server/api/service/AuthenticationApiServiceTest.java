package com.swifty.bank.server.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.swifty.bank.server.api.ConfigureContainer;
import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.LogoutResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.ReissueResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SignOutResponse;
import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenRedisService;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    private static String customerPassword = "987654";
    private static String temporaryToken = "";
    private static String accessToken = "";
    private static String refreshToken = "";

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
                customerPassword, "iPhone", Gender.FEMALE,
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
        secureKeypadService.createSecureKeypad(temporaryToken);

        assertThat(res.getIsAvailable()).isTrue();
        assertThat(!res.getTemporaryToken().isEmpty()).isTrue();
    }

//    @Test
//    @Order(3)
//    public void signUpAndSignInWithNotExistTemporaryToken() {
//        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
//                .deviceId("iPhone")
//                .pushedOrder(encryptPassword(temporaryToken, customerPassword))
//                .build();
//
//        assertThatThrownBy(() -> authenticationApiService.signUpAndSignIn("", reqForSign))
//                .isInstanceOf(NullPointerException.class);
//    }
//
//    @Test
//    @Order(4)
//    public void signUpAndSignInWithNotValidPassword() {
//
//        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
//                .deviceId("iPhone")
//                .pushedOrder(encryptPassword(temporaryToken, "111111"))
//                .build();
//
//        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);
//        assertThat(!res.isSuccess()).isTrue();
//        assertThat(!res.isAvailablePassword()).isTrue();
//        assertThat(res.getTokens() == null).isTrue();
//    }
//
//    @Test
//    @Order(5)
//    public void signUpAndSignInWithValidNewInfo() {
//
//        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
//                .deviceId("iPhone")
//                .pushedOrder(encryptPassword(temporaryToken, customerPassword))
//                .build();
//
//        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);
//        assertThat(res.isAvailablePassword()).isTrue();
//        assertThat(res.isSuccess()).isTrue();
//        assertThat(!res.getTokens().isEmpty()).isTrue();
//    }

//    @Test
//    @Order(6)
//    public void signUpAndSignInWithNotMatchPassword() {
//        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
//                .name("Taylor Swift")
//                .phoneNumber("+821012345678")
//                .mobileCarrier("KT")
//                .residentRegistrationNumber("0101014")
//                .build();
//
//        CheckLoginAvailabilityResponse resForCheck = authenticationApiService.checkLoginAvailability(req);
//        temporaryToken = resForCheck.getTemporaryToken();
//        secureKeypadService.createSecureKeypad(temporaryToken);
//
//        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
//                .deviceId("iPhone")
//                .pushedOrder(encryptPassword(temporaryToken, "381943"))
//                .build();
//
//        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);
//
//        assertThat(res.isSuccess()).isTrue();
//        assertThat(res.isAvailablePassword()).isTrue();
//        assertThat(res.getTokens() == null).isFalse();
//    }

//    @Test
//    @Order(9)
//    public void signUpAndSignInWithDifferentDeviceId() {
//        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
//                .name("Taylor Swift")
//                .phoneNumber("+821012345678")
//                .mobileCarrier("KT")
//                .residentRegistrationNumber("0101014")
//                .build();
//
//        CheckLoginAvailabilityResponse resForCheck = authenticationApiService.checkLoginAvailability(req);
//        temporaryToken = resForCheck.getTemporaryToken();
//        secureKeypadService.createSecureKeypad(temporaryToken);
//
//        customerPassword = "829401";
//
//        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
//                .deviceId("Android")
//                .pushedOrder(encryptPassword(temporaryToken, customerPassword))
//                .build();
//
//        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);
//
//        accessToken = resForSignIn.getTokens().get(0);
//        refreshToken = resForSignIn.getTokens().get(1);
//        assertThat(resForSignIn.isSuccess()).isTrue();
//        assertThat(resForSignIn.isAvailablePassword()).isTrue();
//        assertThat(resForSignIn.getTokens().isEmpty()).isFalse();
//        assertThat(customerService.findByPhoneNumber("+821012345678").isPresent()).isTrue();
//        Customer customer = customerService.findByPhoneNumber("+821012345678").get();
//        assertThat(customer.getDeviceId().equals("Android")).isTrue();
//        assertThat(customer.getName().equals("Taylor Swift")).isTrue();
//        assertThat(encoder.matches(customerPassword, customer.getPassword())).isTrue();
//    }

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

        assertThat(res.getIsSuccessful());
        assertThat(logoutAccessTokenRedisService.getData(accessToken).equals("false"));
    }

//    @Test
//    @Order(14)
//    public void loginAfterLogOutTest() {
//        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
//                .name("Taylor Swift")
//                .phoneNumber("+821012345678")
//                .mobileCarrier("KT")
//                .residentRegistrationNumber("0101014")
//                .build();
//
//        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
//        temporaryToken = res.getTemporaryToken();
//        secureKeypadService.createSecureKeypad(temporaryToken);
//        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
//                .deviceId("Android")
//                .pushedOrder(encryptPassword(temporaryToken, customerPassword))
//                .build();
//
//        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);
//
//        assertThat(resForSignIn.isSuccess());
//        assertThat(resForSignIn.isAvailablePassword());
//        assertThat(!resForSignIn.getTokens().isEmpty());
//        accessToken = resForSignIn.getTokens().get(0);
//        refreshToken = resForSignIn.getTokens().get(1);
//    }

    @Test
    @Order(15)
    public void signOutTest() {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        SignOutResponse res = authenticationApiService.signOut(accessToken);

        assertThat(res.getWasSignedOut());
        assertThat(logoutAccessTokenRedisService.getData(accessToken).equals("false"));
        assertThat(authenticationService.findAuthByCustomerUuid(customerUuid).isEmpty());
        assertThat(customerService.findByUuid(customerUuid).isEmpty());
    }

//    @Test
//    @Order(17)
//    public void signUpTestAfterSignOut() {
//        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
//                .name("Taylor Swift")
//                .phoneNumber("+821012345678")
//                .mobileCarrier("KT")
//                .residentRegistrationNumber("0101014")
//                .build();
//
//        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);
//        temporaryToken = res.getTemporaryToken();
//        secureKeypadService.createSecureKeypad(temporaryToken);
//
//        SignWithFormRequest reqForSign = SignWithFormRequest.builder()
//                .deviceId("Android")
//                .pushedOrder(encryptPassword(res.getTemporaryToken(), customerPassword))
//                .build();
//
//        SignWithFormResponse resForSignIn = authenticationApiService.signUpAndSignIn(temporaryToken, reqForSign);
//
//        assertThat(resForSignIn.isSuccess());
//        assertThat(resForSignIn.isAvailablePassword());
//        assertThat(!resForSignIn.getTokens().isEmpty());
//    }

//    private List<Integer> encryptPassword(String issuedTemporaryToken, String password) {
//        List<Integer> ans = new ArrayList<>();
//        List<Integer> secureKeypadOrderInverse
//                = sBoxKeyRedisService.getData(issuedTemporaryToken)
//                .getKey();
//
//        for (int i = 0; i < 6; i++) {
//            int pos = password.charAt(i) - '0';
//            for (int j = 0; j < secureKeypadOrderInverse.size(); j++) {
//                int key = secureKeypadOrderInverse.get(j);
//                if (key == pos) {
//                    ans.add(j);
//                    break;
//                }
//            }
//        }
//
//        return ans;
//    }
}
