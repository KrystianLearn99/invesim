package org.kris.invesim.simulationms.infrastructure.client;

import org.kris.invesim.PortfolioSpec;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@Component
@HttpExchange(url = "/api/portfolio", accept = "application/json")
public interface PortfolioClient {

    @GetExchange
    PortfolioSpec getPortfolioSpec(@RequestHeader("Authorization") String bearerToken);
}