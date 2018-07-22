package com.n26.realtimestatistics.services;

import com.n26.realtimestatistics.domains.Statistics;
import com.n26.realtimestatistics.domains.TimestampStatistics;
import com.n26.realtimestatistics.domains.Transaction;
import com.n26.realtimestatistics.utils.DateTimeUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.n26.realtimestatistics.utils.DateTimeUtils.isWithinPeriod;

@Service
public class TransactionsAggregator {

    public static final int TransactionTTLInSeconds = 60;

    final ConcurrentHashMap<Integer, TimestampStatistics> bucketedTransactions = new ConcurrentHashMap<>(TransactionTTLInSeconds);

    public Statistics getPeriodStatistics() {

        final DateTime currentDateTime = DateTimeUtils.now();

        double sum = 0;
        long count = 0;
        Optional<Double> min = Optional.empty();
        Optional<Double> max = Optional.empty();

        //aggregate the results of the discrete buckets
        for (TimestampStatistics stats : bucketedTransactions.values()) {
            if (isWithinPeriod(stats.timestamp, currentDateTime, TransactionTTLInSeconds)) {
                sum += stats.sum;
                count += stats.count;
                min = !min.isPresent() || stats.min < min.get() ? Optional.of(stats.min) : min;
                max = !max.isPresent() || stats.max > max.get() ? Optional.of(stats.max) : max;
            }
        }

        return new Statistics(sum, count, min.orElse(0.0), max.orElse(0.0));
    }

    public void register(Transaction transaction, DateTime currentTimestamp) {
        final int secondOfMinute = DateTimeUtils.secondOfMinute(transaction.timestamp);

        bucketedTransactions.compute(secondOfMinute, (bucketKey, stats) -> {

            //transaction didn't happen in the relevant period
            if (!isWithinPeriod(transaction.timestamp, currentTimestamp, TransactionTTLInSeconds)) {
                return stats;
            //the bucket already contains data and it is still relevant, aggregate values
            } else if (stats != null && isWithinPeriod(stats.timestamp, currentTimestamp, TransactionTTLInSeconds)) {
                final double amount = transaction.amount;
                final double min = Math.min(amount, stats.min);
                final double max = Math.max(amount, stats.max);
                return new TimestampStatistics(stats.sum + amount, stats.count + 1,
                        min, max, transaction.timestamp);
            //the bucket was empty or outdated, replace with transaction content
            } else {
                final double amount = transaction.amount;
                return new TimestampStatistics(amount, 1, amount, amount, transaction.timestamp);
            }
        });

    }

}