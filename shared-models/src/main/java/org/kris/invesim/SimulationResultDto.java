package org.kris.invesim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResultDto {
    private UUID simulationId;
    private UUID userId;
    private InvestmentStrategyType strategyType;
    private String email;
    private double expectedFinalValue;
    private double p5;
    private double p50;
    private double p95;
    private double var95Loss;
    private double cvar95Loss;
    private double expectedReturnPct;
    private double p5ReturnPct;
    private double p50ReturnPct;
    private double p95ReturnPct;
    private List<Double> navSample;
}
