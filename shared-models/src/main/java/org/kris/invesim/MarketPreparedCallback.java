package org.kris.invesim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketPreparedCallback {
    private UUID simulationId;
    private UUID userId;
    private String email;
    private String symbol;
    private String bar;
    private StockDataDto stock;
    private EngineSpec engine;
    private StrategySpec strategy;
    private PortfolioSpec portfolio;
}