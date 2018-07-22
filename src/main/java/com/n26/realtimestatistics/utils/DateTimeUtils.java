package com.n26.realtimestatistics.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateTimeUtils {

    public static DateTime now() {
        return DateTime.now(DateTimeZone.UTC);
    }

    public static int secondOfMinute(long timestamp) {
        return new DateTime(timestamp).getSecondOfMinute();
    }

    public static boolean isWithinPeriod(long timestamp,
                                         DateTime currentDateTime,
                                         int ttlInSeconds) {
        final DateTime previousMinuteTimestamp = currentDateTime.minusSeconds(ttlInSeconds);
        return previousMinuteTimestamp.isBefore(timestamp) && !currentDateTime.isBefore(timestamp);
    }

}
