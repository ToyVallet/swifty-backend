package com.swifty.bank.server.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SBoxUtil {
    public static List<Integer> encrypt(List<Integer> plain, List<Integer> key) {
        List<Integer> encrypted = new ArrayList<>();
        for (Integer p : plain) {
            encrypted.add(key.get(p));
        }
        return encrypted;
    }

    public static List<Integer> decrypt(List<Integer> encrypted, List<Integer> key) {
        List<Integer> inverse = Arrays.asList(new Integer[key.size()]);
        for (int i = 0; i < key.size(); i++) {
            inverse.set(key.get(i), i);
        }

        List<Integer> decrypted = new ArrayList<>();
        for (Integer e : encrypted) {
            decrypted.add(inverse.get(e));
        }
        return decrypted;
    }
}
