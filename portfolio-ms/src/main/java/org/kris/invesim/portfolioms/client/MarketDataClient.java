package org.kris.invesim.portfolioms.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.math.BigDecimal;
import java.util.Map;

@HttpExchange(accept = "application/json")
public interface MarketDataClient {

    @GetExchange("/api/quotes")
    Map<String, BigDecimal> getQuotes(@RequestParam("symbols") String symbolsCsv);
}