package com.n26.realtimestatistics.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n26.realtimestatistics.domains.Statistics;
import com.n26.realtimestatistics.services.TransactionsAggregator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
@AutoConfigureJson
@AutoConfigureJsonTesters
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<Statistics> json;

    @MockBean
    private TransactionsAggregator transactionsAggregator;

    @Test
    public void respondWithPeriodStatistics() throws Exception {
        final Statistics statitics = new Statistics(30.32, 4, 2.57, 329.32);
        when(transactionsAggregator.getPeriodStatistics()).thenReturn(statitics);
        this.mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().json(json.write(statitics).getJson()));
    }
}