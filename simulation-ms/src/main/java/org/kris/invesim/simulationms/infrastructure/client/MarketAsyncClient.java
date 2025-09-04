package org.kris.invesim.simulationms.infrastructure.client;

import org.kris.invesim.PrepareMarketRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
@HttpExchange(url = "/api/market-data", accept = "application/json")
public interface MarketAsyncClient {

    @PostExchange("/prepare")
    ResponseEntity<Void> prepareMarketData(@RequestHeader("Authorization") String bearerToken,
            @RequestBody PrepareMarketRequest request);

}
