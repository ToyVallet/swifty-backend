package com.swifty.bank.server.core.domain.sms.service.impl;

import com.swifty.bank.server.core.common.utils.RandomUtil;
import com.swifty.bank.server.core.common.utils.RedisUtil;
import com.swifty.bank.server.core.common.utils.StringUtil;
import com.swifty.bank.server.core.domain.sms.constant.MessageStatus;
import com.swifty.bank.server.core.domain.sms.service.VerifyService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyServiceImpl implements VerifyService {
    private final TwilioMessageService messageService;
    private final RedisUtil redisUtil;

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

        String redisKey = createRedisKeyForOtp(phoneNumber);
        redisUtil.setRedisStringValue(
                redisKey,
                otp
        );
        redisUtil.setRedisStringExpiration(redisKey, 5, TimeUnit.MINUTES);

        return true;
    }

    @Override
    public Boolean checkVerificationCode(String phoneNumber, String expectedOtp) {
        String redisKey = createRedisKeyForOtp(phoneNumber);
        String actualOtp = redisUtil.getRedisStringValue(redisKey);

        // 기간이 만료되었거나 인증번호를 발송한 적이 없는 경우
        if (actualOtp == null || actualOtp.isEmpty()) {
            return false;
        }

        // 인증번호가 불일치한 경우
        if (!actualOtp.equals(expectedOtp)) {
            return false;
        }

        redisUtil.setRedisStringValue(
                redisKey,
                "true"
        );
        // 인증 성공한 시기로부터 10분간만 인증 유효하도록 함
        redisUtil.setRedisStringExpiration(redisKey, 10, TimeUnit.MINUTES);
        return true;
    }

    public String createRedisKeyForOtp(String str) {
        return StringUtil.joinString(
                List.of("otp-", str)
        );
    }
}
