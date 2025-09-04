package org.kris.invesim.simulationms.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.kris.invesim.EngineSpec;
import org.kris.invesim.StrategySpec;


@Data
public class SimulationRequestForPortfolioDto {

    @NotNull
    private StrategySpec strategy;

    @NotNull
    private EngineSpec engine;

}