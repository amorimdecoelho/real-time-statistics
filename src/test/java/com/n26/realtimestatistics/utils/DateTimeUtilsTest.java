package com.n26.realtimestatistics.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DateTimeUtilsTest {

    @Test
    public void currentTimeInUTCTimeZone() throws Exception {
        assert(DateTimeUtils.now().getZone()).equals(DateTimeZone.UTC);
    }

    @Test
    public void convertTimestampToSecondOfMinute() throws Exception {
        final DateTime now = DateTime.now();
        Assert.assertEquals(now.getSecondOfMinute(), DateTimeUtils.secondOfMinute(now.getMillis()));
    }

    @Test
    public void acceptTimestampWithinPeriod() throws Exception {
        final DateTime now = DateTimeUtils.now();
        final long recentTimestamp = now.minusSeconds(10).getMillis();
        Assert.assertTrue(DateTimeUtils.isWithinPeriod(recentTimestamp, now, 60));
    }

    @Test
    public void rejectTimestampBeforePeriod() throws Exception {
        final DateTime now = DateTimeUtils.now();
        final long recentTimestamp = now.minusSeconds(70).getMillis();
        Assert.assertFalse(DateTimeUtils.isWithinPeriod(recentTimestamp, now, 60));
    }

    @Test
    public void rejectTimestampInTheFuture() throws Exception {
        final DateTime now = DateTimeUtils.now();
        final long recentTimestamp = now.minusSeconds(-1).getMillis();
        Assert.assertFalse(DateTimeUtils.isWithinPeriod(recentTimestamp, now, 60));
    }


}
