package org.kris.invesim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationStartedDto {
    private UUID simulationId;
    private UUID userId;
    private String email;
    private String symbol;
    private InvestmentStrategyType strategyType;

}
