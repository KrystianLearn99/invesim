package org.kris.invesim.simulationms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.kris.invesim.InvestmentStrategyType;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class SimulationResult {
    private final UUID runId;
    private final InvestmentStrategyType strategyType;

    private final double expectedFinalValue;
    private final double p5;
    private final double p50;
    private final double p95;

    private final double var95;
    private final double cvar95;

    private final List<Double> samplePath;

    private final double var95Loss;
    private final double cvar95Loss;

    private final double expectedReturnPct;
    private final double p5ReturnPct;
    private final double p50ReturnPct;
    private final double p95ReturnPct;

    private final List<Double> navSample;
}

