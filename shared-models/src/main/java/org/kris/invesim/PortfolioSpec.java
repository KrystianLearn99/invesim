package org.kris.invesim;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.util.List;

@Data
public class PortfolioSpec {
    @DecimalMin("0.0")
    private double cash;

    private List<PositionDto> positions;
}
