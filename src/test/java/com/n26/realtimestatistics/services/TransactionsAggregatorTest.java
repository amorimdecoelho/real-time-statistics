package com.n26.realtimestatistics.services;


import com.n26.realtimestatistics.domains.Statistics;
import com.n26.realtimestatistics.domains.TimestampStatistics;
import com.n26.realtimestatistics.domains.Transaction;
import com.n26.realtimestatistics.utils.DateTimeUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TransactionsAggregatorTest {

    private final DateTime now = DateTimeUtils.now();

    private final Transaction transaction1 = new Transaction(10.0, now.minusSeconds(10).getMillis());
    private final Transaction transaction2 = new Transaction(20.0, now.minusMillis(10050).getMillis());
    private final Transaction transaction3 = new Transaction(150.0, now.minusSeconds(20).getMillis());
    private final Transaction transaction4 = new Transaction(60.0, now.minusSeconds(70).getMillis());
    private final Transaction transaction5 = new Transaction(70.0, now.plusSeconds(30).getMillis());

    @Test
    public void registerTransaction() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction1, now);

        final double amount = transaction1.amount;

        final TimestampStatistics statistics = transactionsAggregator.bucketedTransactions.get(DateTimeUtils.secondOfMinute(transaction1.timestamp));
        final TimestampStatistics expected = new TimestampStatistics(amount, 1, amount, amount, transaction1.timestamp);

        Assert.assertEquals(expected, statistics);
    }

    @Test
    public void registerMultipleTransactionsInSingleSlot() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction1, now);
        transactionsAggregator.register(transaction2, now);

        final double amount1 = transaction1.amount;
        final double amount2 = transaction2.amount;

        final TimestampStatistics statistics = transactionsAggregator.bucketedTransactions.get(DateTimeUtils.secondOfMinute(transaction2.timestamp));
        final TimestampStatistics expected = new TimestampStatistics(amount1 + amount2, 2, amount1, amount2, transaction2.timestamp);

        Assert.assertEquals(expected, statistics);
    }

    @Test
    public void registerMultipleTransactionsInDifferentSlots() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction1, now);
        transactionsAggregator.register(transaction2, now);
        transactionsAggregator.register(transaction3, now);

        final double amount1 = transaction1.amount;
        final double amount2 = transaction2.amount;
        final double amount3 = transaction3.amount;

        final TimestampStatistics statistics1 = transactionsAggregator.bucketedTransactions.get(DateTimeUtils.secondOfMinute(transaction2.timestamp));
        final TimestampStatistics expected1 = new TimestampStatistics(amount1 + amount2, 2, amount1, amount2, transaction2.timestamp);

        Assert.assertEquals(expected1, statistics1);


        final TimestampStatistics statistics2 = transactionsAggregator.bucketedTransactions.get(DateTimeUtils.secondOfMinute(transaction3.timestamp));
        final TimestampStatistics expected2 = new TimestampStatistics(amount3, 1, amount3, amount3, transaction3.timestamp);

        Assert.assertEquals(expected2, statistics2);

        Assert.assertEquals(2, transactionsAggregator.bucketedTransactions.size());

    }

    @Test
    public void replaceOldTransaction() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction4, now.minusSeconds(60));

        final double amount1 = transaction4.amount;
        final double amount2 = transaction1.amount;

        final TimestampStatistics statistics1 = transactionsAggregator.bucketedTransactions.get(DateTimeUtils.secondOfMinute(transaction4.timestamp));
        final TimestampStatistics expected1 = new TimestampStatistics(amount1, 1, amount1, amount1, transaction4.timestamp);

        Assert.assertEquals(expected1, statistics1);

        transactionsAggregator.register(transaction1, now);

        final TimestampStatistics statistics2 = transactionsAggregator.bucketedTransactions.get(DateTimeUtils.secondOfMinute(transaction4.timestamp));
        final TimestampStatistics expected2 = new TimestampStatistics(amount2, 1, amount2, amount2, transaction1.timestamp);

        Assert.assertEquals(expected2, statistics2);
    }

    @Test
    public void ignoreOldTransaction() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction4, now);

        final TimestampStatistics statistics = transactionsAggregator.bucketedTransactions.get(DateTimeUtils.secondOfMinute(transaction4.timestamp));

        Assert.assertNull(statistics);
    }

    @Test
    public void ignoreFutureTransaction() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction5, now);

        final TimestampStatistics statistics = transactionsAggregator.bucketedTransactions.get(DateTimeUtils.secondOfMinute(transaction5.timestamp));

        Assert.assertNull(statistics);
    }

    @Test
    public void getPeriodStatisticsWithSingleSlot() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction1, now);
        transactionsAggregator.register(transaction2, now);

        final Statistics statistics = transactionsAggregator.getPeriodStatistics();

        final Statistics expected = new Statistics(30, 2, 10, 20);

        Assert.assertEquals(expected, statistics);
    }

   @Test
    public void getPeriodStatisticsWithMultipleSlots() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction1, now);
        transactionsAggregator.register(transaction2, now);
        transactionsAggregator.register(transaction3, now);
        transactionsAggregator.register(transaction4, now);
        transactionsAggregator.register(transaction5, now);

        final Statistics statistics = transactionsAggregator.getPeriodStatistics();

        final Statistics expected = new Statistics(180, 3, 10, 150);

        Assert.assertEquals(expected, statistics);
    }

    @Test
    public void getPeriodStatisticsForEmptyPeriod() throws Exception {
        final TransactionsAggregator transactionsAggregator = new TransactionsAggregator();

        transactionsAggregator.register(transaction4, now);
        transactionsAggregator.register(transaction5, now);

        final Statistics statistics = transactionsAggregator.getPeriodStatistics();

        final Statistics expected = new Statistics(0, 0, 0, 0);

        Assert.assertEquals(expected, statistics);
    }

}
