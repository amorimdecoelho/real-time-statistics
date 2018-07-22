package com.n26.realtimestatistics.api;

import com.n26.realtimestatistics.domains.Statistics;
import com.n26.realtimestatistics.domains.Transaction;
import com.n26.realtimestatistics.utils.DateTimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJson
@AutoConfigureJsonTesters
public class SmokeTest {

    private Transaction createTransaction(double amount, int secondsOffset) {
        return new Transaction(amount, DateTimeUtils.now().minusSeconds(secondsOffset).getMillis());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<Statistics> statisticsJson;

    @Autowired
    private JacksonTester<Transaction> transactionJson;


    @Test
    public void respondWithEmptyStatistics() throws Exception {
        this.mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"sum\": 0, \"count\": 0, \"min\": 0, \"max\": 0, \"avg\": 0 }"));
    }

    @Test
    public void respondWithFilledStatistics() throws Exception {

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson.write(createTransaction(30, 10)).getJson()))
                .andExpect(status().isCreated());

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson.write(createTransaction(40, 20)).getJson()))
                .andExpect(status().isCreated());

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson.write(createTransaction(70, 15)).getJson()))
                .andExpect(status().isCreated());

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson.write(createTransaction(150, 75)).getJson()))
                .andExpect(status().isNoContent());

        final Statistics statistics = new Statistics(140, 3, 30, 70);

        this.mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().json(statisticsJson.write(statistics).getJson()));
    }

}
