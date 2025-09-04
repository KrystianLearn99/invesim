package org.kris.invesim.simulationms.infrastructure.client;

import org.kris.invesim.StockDataDto;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@Component
@HttpExchange(url = "/api/market-data", accept = "application/json")
public interface MarketDataClient {

    @GetExchange("/{symbol}/history")
    StockDataDto fetchHistory(@RequestHeader("Authorization") String bearerToken,
                              @PathVariable("symbol") String symbol
    );
}
