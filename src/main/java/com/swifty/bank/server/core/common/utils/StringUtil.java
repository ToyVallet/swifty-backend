package com.swifty.bank.server.core.common.utils;

import java.util.List;

public class StringUtil {
    public static String joinString(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        strings.forEach(sb::append);
        return sb.toString();
    }
}
