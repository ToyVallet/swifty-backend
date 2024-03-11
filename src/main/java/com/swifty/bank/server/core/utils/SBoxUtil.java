package com.swifty.bank.server.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SBoxUtil {
    public static void validateKey(List<Integer> key) {
        Integer max = Collections.max(key);
        Integer min = Collections.min(key);

        if (!(min.equals(1) && max.equals(key.size()))) {
            throw new IllegalArgumentException("올바르지 않은 키값 입니다.");
        }
    }

    public static List<Integer> encrypt(List<Integer> plain, List<Integer> key) {
        validateKey(key);
        
        return new ArrayList<>() {{
            for (Integer p : plain) {
                add(key.get(p));
            }
        }};
    }

    public static List<Integer> decrypt(List<Integer> encrypted, List<Integer> key) {
        validateKey(key);

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
