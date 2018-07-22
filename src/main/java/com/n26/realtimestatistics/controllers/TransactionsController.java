package com.n26.realtimestatistics.controllers;

import com.n26.realtimestatistics.domains.Transaction;
import com.n26.realtimestatistics.services.TransactionsAggregator;
import com.n26.realtimestatistics.utils.DateTimeUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static com.n26.realtimestatistics.services.TransactionsAggregator.TransactionTTLInSeconds;
import static com.n26.realtimestatistics.utils.DateTimeUtils.isWithinPeriod;

@RestController
public class TransactionsController {

    @Autowired
    private TransactionsAggregator transactionsAggregator;

    @RequestMapping(method=POST, path = "/transactions")
    public ResponseEntity<String> register(@RequestBody Transaction transaction) {

        final DateTime currentDateTime = DateTimeUtils.now();

        if (isWithinPeriod(transaction.timestamp, currentDateTime, TransactionTTLInSeconds)) {
            transactionsAggregator.register(transaction, currentDateTime);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

}
