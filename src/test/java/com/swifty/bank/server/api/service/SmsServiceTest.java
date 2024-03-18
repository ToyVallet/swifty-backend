package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.ConfigureContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
@SpringBootTest
public class SmsServiceTest extends ConfigureContainer {
//    @Autowired
//    private OtpRedisService otpRedisService;
//    @Autowired
//    private SmsService smsService;
//
//
//    @Test
//    public void stealVerificationCodeTest() {
//        String phoneNumber = "+821012345678";
//        StealVerificationCodeRequest stealVerificationCodeRequest = new StealVerificationCodeRequest(phoneNumber);
//
//        StealVerificationCodeResponse res = smsService.stealVerificationCode(stealVerificationCodeRequest);
//        assertThat(res.getOtp()).isEqualTo(otpRedisService.getData(phoneNumber));
//    }
//
//    @Test
//    public void checkNotValidVerificationCodeTest() {
//        String phoneNumber = "+821012345678";
//        String verificationCode = "000000";
//
//        CheckVerificationCodeRequest req = new CheckVerificationCodeRequest(phoneNumber, verificationCode);
//
//        assertThat(!smsService.checkVerificationCode(req).getIsSuccess());
//    }
//
//    @Test
//    public void checkValidVerificationCodeTest() {
//        String phoneNumber = "+821012345678";
//        String verificationCode = "000000";
//
//        CheckVerificationCodeRequest req = new CheckVerificationCodeRequest(phoneNumber, verificationCode);
//        StealVerificationCodeRequest reqForSteal = new StealVerificationCodeRequest(phoneNumber);
//
//        smsService.stealVerificationCode(reqForSteal);
//
//        assertThat(smsService.checkVerificationCode(req).getIsSuccess());
//    }
}
