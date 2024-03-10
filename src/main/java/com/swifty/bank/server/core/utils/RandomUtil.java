package com.swifty.bank.server.core.utils;

import java.util.Random;

public class RandomUtil {
    public static String generateOtp(int len) {
        StringBuilder sb = new StringBuilder();
        long seed = System.currentTimeMillis();
        long salt = getRandomNumber(1, 99);
        Random random = new Random(seed + salt);
        for (int i = 0; i < len; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}