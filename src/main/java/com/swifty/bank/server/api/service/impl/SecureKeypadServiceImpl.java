package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.keypad.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.service.SecureKeypadService;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import com.swifty.bank.server.core.utils.SBoxUtil;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecureKeypadServiceImpl implements SecureKeypadService {
    private final Map<String, Integer> keypadPathDict = new HashMap<>() {{
        put("assets/images/keypad/0.svg", 0);
        put("assets/images/keypad/1.svg", 1);
        put("assets/images/keypad/2.svg", 2);
        put("assets/images/keypad/3.svg", 3);
        put("assets/images/keypad/4.svg", 4);
        put("assets/images/keypad/5.svg", 5);
        put("assets/images/keypad/6.svg", 6);
        put("assets/images/keypad/7.svg", 7);
        put("assets/images/keypad/8.svg", 8);
        put("assets/images/keypad/9.svg", 9);
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
    private final SBoxKeyRedisService sBoxKeyRedisService;

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

    @Override
    public CreateSecureKeypadResponse createSecureKeypad(String temporaryToken) {
        final int keypadSize = keypadFiles.size();

        // 각 키패드 이미지에 대응하는 숫자 추출
        // ex) "assets/images/keypad/1.svg"는 숫자 1을 의미
        List<Integer> plainOrder = new ArrayList<>() {{
            for (String path : keypadPaths) {
                add(keypadPathDict.get(path));
            }
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

        // redis에 섞은 순서에 대한 정보 저장
        sBoxKeyRedisService.setData(
                temporaryToken,
                SBoxKey.builder()
                        .key(key)
                        .build()
        );
        return CreateSecureKeypadResponse.builder()
                .keypad(keypad)
                .build();
    }
}