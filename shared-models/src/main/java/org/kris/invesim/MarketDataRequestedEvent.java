package org.kris.invesim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataRequestedEvent {
    private UUID simulationId;
    private UUID userId;
    private String email;
    private String symbol;
    private String bar;
    private EngineSpec engine;
    private StrategySpec strategy;
    private PortfolioSpec portfolio;
}
