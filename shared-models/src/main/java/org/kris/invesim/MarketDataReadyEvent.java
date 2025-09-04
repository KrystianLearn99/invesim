package org.kris.invesim;

import lombok.*;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataReadyEvent {
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