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
public class SimulationPortfolioRequestedDto {
    private UUID simulationId;
    private UUID userId;
    private String email;
    private InvestmentStrategyType strategyType;
    private EngineSpec engine;
    private String bar;
}
