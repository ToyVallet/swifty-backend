package com.swifty.bank.server.core.domain.sms.service.impl;

import com.swifty.bank.server.core.common.redis.service.impl.OtpRedisServiceImpl;
import com.swifty.bank.server.core.domain.sms.constant.MessageStatus;
import com.swifty.bank.server.core.domain.sms.service.VerifyService;
import com.swifty.bank.server.core.utils.RandomUtil;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyServiceImpl implements VerifyService {
    private final TwilioMessageService messageService;
    private final OtpRedisServiceImpl redisService;

    private final Long verificationTimeout = 5L;
    private final Long verificationRetentionTimeout = 10L;

    @Override
    public Boolean sendVerificationCode(String phoneNumber) {
        String otp = RandomUtil.generateOtp(6);
        MessageStatus messageStatus = messageService.sendMessage(
                phoneNumber,
                "[SWIFTY 뱅크] 회원가입 인증번호는 " + otp + "입니다."
        );

        // 메세지 전송 자체를 실패한 경우
        if (messageStatus.equals(MessageStatus.FAILED)) {
            return false;
        }

        redisService.setData(
                phoneNumber,
                otp,
                verificationTimeout,
                TimeUnit.MINUTES
        );
        return true;
    }

    @Override
    public Boolean checkVerificationCode(String phoneNumber, String expectedOtp) {
        String actualOtp = redisService.getData(phoneNumber);

        // 기간이 만료되었거나 인증번호를 발송한 적이 없는 경우
        if (actualOtp == null || actualOtp.isEmpty()) {
            return false;
        }

        // 인증번호가 불일치한 경우
        if (!actualOtp.equals(expectedOtp)) {
            return false;
        }

        // 인증 성공한 시기로부터 10분간만 인증 유효하도록 함
        redisService.setData(
                phoneNumber,
                "true",
                verificationRetentionTimeout,
                TimeUnit.MINUTES
        );
        return true;
    }


    @Override
    public boolean isVerified(String phoneNumber) {
        String isVerified = redisService.getData(phoneNumber);
        // 만료 되어서 사라졌거나 인증이 된 상태가 아닌 경우
        return isVerified != null && isVerified.equals("true");
    }
}