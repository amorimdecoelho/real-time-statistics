package com.n26.realtimestatistics.domains;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class StatisticsTest {

    @Test
    public void calculateAverage() throws Exception {
        final double avg = new Statistics(9, 2, 0, 10).avg();
        Assert.assertEquals(4.5, avg, 0);
    }

    @Test
    public void zeroAverageWhenCountIsZero() throws Exception {
        final double avg = new Statistics(10, 0, 5, 10).avg();
        Assert.assertEquals(0, avg, 0);
    }

}
