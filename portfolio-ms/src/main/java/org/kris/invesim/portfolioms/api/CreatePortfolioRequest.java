package org.kris.invesim.portfolioms.api;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePortfolioRequest(
        @NotNull BigDecimal initialCash
) {}