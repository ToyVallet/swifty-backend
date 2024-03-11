package com.swifty.bank.server.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SBoxUtil {

    // key는 [0,key.size()-1] 범위를 모두 포함한 경우만 허용
    public static void validateKey(List<Integer> key) {
        boolean isInvalid = false;
        for (int i = 0; i < key.size(); i++) {
            isInvalid |= !key.contains(i);
        }

        if (isInvalid) {
            throw new IllegalArgumentException(key + ": 올바르지 않은 키값 입니다.");
        }
    }

    public static void validateTarget(List<Integer> target, List<Integer> key) {
        Integer min = Collections.min(key);
        Integer max = Collections.max(key);

        if (Collections.min(target) < min || Collections.max(target) > max) {
            throw new IllegalArgumentException(target + ": 올바르지 않은 타겟입니다.");
        }
    }

    public static List<Integer> encrypt(List<Integer> plain, List<Integer> key) {
        validateKey(key);
        validateTarget(plain, key);
        return new ArrayList<>() {{
            for (Integer p : plain) {
                add(key.get(p));
            }
        }};
    }

    public static List<Integer> decrypt(List<Integer> encrypted, List<Integer> key) {
        validateKey(key);
        validateTarget(encrypted, key);

        List<Integer> inverse = Arrays.asList(new Integer[key.size()]);
        for (int i = 0; i < key.size(); i++) {
            inverse.set(key.get(i), i);
        }

        return new ArrayList<>() {{
            for (Integer e : encrypted) {
                add(inverse.get(e));
            }
        }};
    }
}
