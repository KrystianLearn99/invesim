package org.kris.invesim;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class EngineSpec {
    @Min(1) @Max(200_000)
    private int simulations;

    @Min(1) @Max(10_000)
    private int steps;

    private String step;

    @DecimalMin("0.0")
    private double riskFreeRate;

    private long seed = 42L;

    private boolean useCorrelation = false;
}