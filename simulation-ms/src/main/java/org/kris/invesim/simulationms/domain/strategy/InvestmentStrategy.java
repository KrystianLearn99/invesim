package org.kris.invesim.simulationms.domain.strategy;

import org.kris.invesim.simulationms.domain.model.PortfolioState;

import java.time.LocalDate;
import java.util.Map;

public interface InvestmentStrategy {

    default void onStart(PortfolioState state, StrategyContext ctx) {}

    void onStep(int stepIndex, LocalDate date, Map<String, Double> prices, PortfolioState state, StrategyContext ctx);

    default void onFinish(PortfolioState state, StrategyContext ctx) {}

    record StrategyContext(double dtYears, Map<String, Double> startPrices) {}
}




