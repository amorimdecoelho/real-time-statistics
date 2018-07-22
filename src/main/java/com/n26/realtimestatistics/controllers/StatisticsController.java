package com.n26.realtimestatistics.controllers;

import com.n26.realtimestatistics.domains.Statistics;
import com.n26.realtimestatistics.services.TransactionsAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class StatisticsController {

    @Autowired
    private TransactionsAggregator transactionsAggregator;

    @RequestMapping(method=GET, path = "/statistics")
    public Statistics getStatistics() {
        return transactionsAggregator.getPeriodStatistics();
    }

}
