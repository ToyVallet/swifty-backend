package com.swifty.bank.server.core.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {
    public static Date now() {
        return new Date();
//        return getNowFromLocalDate();
    }

    private static Date getNowFromLocalDate() {
        // convert LocalDate to Date
        Instant instant = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date millisToDate(long milliseconds) {
        return new Date(milliseconds);
    }

    public static Long diffInSeconds(Date d1, Date d2) {
        return (d1.getTime() - d2.getTime()) / 1000;
    }
}
