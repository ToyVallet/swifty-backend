package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.keypad.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.service.SecureKeypadService;
import com.swifty.bank.server.core.common.redis.service.SecureKeypadOrderInverseRedisService;
import com.swifty.bank.server.core.common.redis.value.SecureKeypadOrderInverse;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecureKeypadServiceImpl implements SecureKeypadService {
    private final SecureKeypadOrderInverseRedisService secureKeypadOrderInverseRedisService;
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

    @Override
    public CreateSecureKeypadResponse createSecureKeypad(String temporaryToken) {
        int keypadSize = keypadFiles.size();
        String[] keypad = new String[keypadSize];
        List<Integer> keypadOrder = new ArrayList<>(IntStream.range(0, keypadSize).boxed().toList());

        // 키패드 순서 셔플
        Collections.shuffle(keypadOrder);

        for (int i = 0; i < keypadSize; i++) {
            keypad[i] = keypadFiles.get(keypadOrder.get(i));
        }
        List<Integer> keypadOrderInverse = Arrays.asList(new Integer[10]);
        for (int i = 0; i < keypadSize; i++) {
            keypadOrderInverse.set(keypadOrder.get(i), i);
        }

        // redis에 키패드 셔플 정보 저장
        secureKeypadOrderInverseRedisService.setData(
                temporaryToken,
                SecureKeypadOrderInverse.builder()
                        .keypadOrderInverse(keypadOrder)
                        .build()
        );

        return CreateSecureKeypadResponse.builder()
                .keypad(keypad)
                .build();
    }
}