package com.swifty.bank.server.utils;

import java.util.List;

public class HashUtil {
    public static String createStringHash(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        strings.forEach(sb::append);
        return sb.toString();
    }
}
