package com.swifty.bank.server.core.domain.keypad.service.impl;

import com.swifty.bank.server.core.domain.keypad.service.SecureKeypadService;
import com.swifty.bank.server.core.domain.keypad.service.dto.SecureKeypadDto;
import com.swifty.bank.server.core.utils.DateUtil;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.core.utils.SBoxUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecureKeypadServiceImpl implements SecureKeypadService {
    @Value("${jwt.keypad-token-expiration-seconds}")
    private Long keypadTokenExpiration;
    private final Map<Integer, String> keypadPathDict = new HashMap<>() {{
        put(0, "assets/images/keypad/0.svg");
        put(1, "assets/images/keypad/1.svg");
        put(2, "assets/images/keypad/2.svg");
        put(3, "assets/images/keypad/3.svg");
        put(4, "assets/images/keypad/4.svg");
        put(5, "assets/images/keypad/5.svg");
        put(6, "assets/images/keypad/6.svg");
        put(7, "assets/images/keypad/7.svg");
        put(8, "assets/images/keypad/8.svg");
        put(9, "assets/images/keypad/9.svg");
    }};
    private final List<String> keypadPaths = List.of(
            "assets/images/keypad/0.svg",
            "assets/images/keypad/1.svg",
            "assets/images/keypad/2.svg",
            "assets/images/keypad/3.svg",
            "assets/images/keypad/4.svg",
            "assets/images/keypad/5.svg",
            "assets/images/keypad/6.svg",
            "assets/images/keypad/7.svg",
            "assets/images/keypad/8.svg",
            "assets/images/keypad/9.svg"
    );
    private final List<String> keypadFiles;

    @PostConstruct
    private void init() {
        if (keypadPathDict.size() != keypadPaths.size()) {
            throw new IllegalArgumentException("키패드 이미지에 숫자를 일대일대응 시켜주세요.");
        }

        keypadFiles.clear();
        keypadPaths.forEach(keypadPath ->
                {
                    try {
                        ClassPathResource resource = new ClassPathResource(keypadPath);
                        InputStream inputStream = resource.getInputStream();
                        byte[] contentBytes = inputStream.readAllBytes();
                        String content = new String(contentBytes, StandardCharsets.UTF_8);
                        keypadFiles.add(content);
                    } catch (IOException e) {
                        throw new RuntimeException("키패드 이미지를 읽어오는데 실패했습니다.");
                    }
                }
        );
    }

    // 0~9 숫자가 적힌 이미지를 랜덤으로 섞어 반환
    @Override
    public SecureKeypadDto createSecureKeypad() {
        final int keypadSize = keypadFiles.size();

        // len 만큼
        List<Integer> plainOrder = new ArrayList<>() {{
            this.addAll(keypadPathDict.keySet().stream().sorted().toList());
        }};

        // 키패드 섞을 순서(key) 결정 (랜덤)
        List<Integer> key = SBoxUtil.generateKey(keypadSize);

        // 순서 섞기
        List<Integer> shuffledOrder = SBoxUtil.encrypt(plainOrder, key);

        // 순서 섞인 키패드 이미지 파일 리스트 생성
        List<String> keypad = Arrays.asList(new String[keypadSize]);
        for (int i = 0; i < key.size(); i++) {
            keypad.set(shuffledOrder.get(i), keypadFiles.get(i));
        }

        return SecureKeypadDto.builder()
                .key(key)
                .shuffledKeypadImages(keypad)
                .build();
    }

    @Override
    public String createKeypadToken() {
        Claims claims = Jwts.claims();
        Date expiration = DateUtil.millisToDate(DateUtil.now().getTime() + keypadTokenExpiration * 1000L);

        claims.setSubject("keypad-token");
        return JwtUtil.generateToken(claims, expiration);
    }
}