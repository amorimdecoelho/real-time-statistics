package com.n26.realtimestatistics.controllers;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n26.realtimestatistics.domains.Transaction;
import com.n26.realtimestatistics.services.TransactionsAggregator;
import com.n26.realtimestatistics.utils.DateTimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionsController.class)
@AutoConfigureJson
@AutoConfigureJsonTesters
public class TransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionsAggregator transactionsAggregator;

    @Autowired
    private JacksonTester<Transaction> json;

    private Transaction createTransaction(double amount, int secondsOffset) {
        return new Transaction(amount, DateTimeUtils.now().minusSeconds(secondsOffset).getMillis());
    }

    @Test
    public void registerRecentTransaction() throws Exception {
        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.write(createTransaction(30, 10)).getJson()))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    public void ignoreOldTransaction() throws Exception {
        this.mockMvc.perform(
                post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.write(createTransaction(30, 70)).getJson()))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

    @Test
    public void ignoreFutureTransaction() throws Exception {
        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.write(createTransaction(30, -10)).getJson()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void failForTransactionWithoutTimestamp() throws Exception {
        final String jsonTransaction = "\"amount\": 30.42";

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTransaction))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void failForTransactionWithoutAmount() throws Exception {
        final String jsonTransaction = "\"timestamp\": 1532193457000";

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTransaction))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void failForTransactionWithInvalidTimestamp() throws Exception {
        final String jsonTransaction = "\"timestamp\": \"text\"";

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTransaction))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void failForTransactionWithInvalidAmount() throws Exception {
        final String jsonTransaction = "\"amount\": true";

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTransaction))
                .andExpect(status().is4xxClientError());
    }

}