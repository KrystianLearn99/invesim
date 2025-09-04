package org.kris.invesim.portfolioms.api;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TradeRequest(
        @NotBlank String symbol,
        @NotNull BigDecimal quantity,
        @NotNull BigDecimal price
) {}