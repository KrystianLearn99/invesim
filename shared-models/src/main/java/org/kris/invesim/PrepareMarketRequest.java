package org.kris.invesim;

import java.util.UUID;

public record PrepareMarketRequest(
        UUID simulationId,
        UUID userId,
        String email,
        String symbol,
        String bar,
        EngineSpec engine,
        StrategySpec strategy,
        PortfolioSpec portfolio,
        String callbackUrl
) {}