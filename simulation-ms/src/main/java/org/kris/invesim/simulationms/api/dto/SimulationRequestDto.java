package org.kris.invesim.simulationms.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.kris.invesim.EngineSpec;
import org.kris.invesim.MarketSpec;
import org.kris.invesim.PortfolioSpec;
import org.kris.invesim.StrategySpec;


@Data
@Builder
public class SimulationRequestDto {

    @NotNull
    private StrategySpec strategy;

    @NotNull
    private MarketSpec market;

    @NotNull
    private EngineSpec engine;

    @NotNull
    private PortfolioSpec portfolio;
}