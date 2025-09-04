package org.kris.invesim.simulationms.infrastructure.client;

import org.kris.invesim.EngineSpec;
import org.kris.invesim.StrategySpec;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

@Component
@HttpExchange(url = "/api/portfolio", accept = "application/json")
public interface PortfolioAsyncClient {

    @PostExchange("/prepare")
    ResponseEntity<Void> preparePortfolio(@RequestHeader("Authorization") String bearerToken,
                                          @RequestBody PreparePortfolioRequest request);

    record PreparePortfolioRequest(
            UUID simulationId,
            String userId,
            EngineSpec engine,
            StrategySpec strategy,
            String callbackUrl
    ) {}
}
