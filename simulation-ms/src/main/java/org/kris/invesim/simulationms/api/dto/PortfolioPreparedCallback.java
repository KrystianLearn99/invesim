package org.kris.invesim.simulationms.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kris.invesim.EngineSpec;
import org.kris.invesim.PortfolioSpec;
import org.kris.invesim.StrategySpec;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioPreparedCallback {
    private UUID simulationId;
    private UUID userId;
    private PortfolioSpec portfolio;
    private String symbol;
    private String bar;
    private EngineSpec engine;
    private StrategySpec strategy;
}