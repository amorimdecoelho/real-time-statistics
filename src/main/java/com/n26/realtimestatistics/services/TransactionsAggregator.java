package com.n26.realtimestatistics.services;

import com.n26.realtimestatistics.domains.Statistics;
import com.n26.realtimestatistics.domains.Transaction;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class TransactionsAggregator {

    public static final int TransactionTTLInSeconds = 60;

    public Statistics getPeriodStatistics() {
        return new Statistics(0, 0, 0,0);
    }

    public void register(Transaction transaction, DateTime currentTimestamp) {

    }

}
