package org.kris.invesim.simulationms.domain.strategy;

import org.kris.invesim.simulationms.domain.model.PortfolioState;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DcaStrategy implements InvestmentStrategy {
    private final double amountPerStep;
    private final List<String> symbols;
    private final int intervalSteps;

    public DcaStrategy(double amountPerStep, List<String> symbols) {
        this(amountPerStep, symbols, 1);
    }

    public DcaStrategy(double amountPerStep, List<String> symbols, int intervalSteps) {
        this.amountPerStep = amountPerStep;
        this.symbols = symbols;
        this.intervalSteps = Math.max(1, intervalSteps);
    }

    @Override
    public void onStep(int stepIndex, LocalDate date, Map<String, Double> prices,
                       PortfolioState state, StrategyContext ctx) {
        if (amountPerStep <= 0 || symbols == null || symbols.isEmpty()) return;

        if (stepIndex % intervalSteps != 0) return;

        double budget = Math.min(amountPerStep, state.cash());
        if (budget <= 0) return;

        int n = symbols.size();
        double perSymbol = budget / n;

        double spent = 0.0;
        for (String s : symbols) {
            double p = prices.getOrDefault(s, 0.0);
            if (p > 0 && state.cash() >= perSymbol) {
                state.buy(s, p, perSymbol);
                spent += perSymbol;
            }
        }

        double leftover = budget - spent;
        if (leftover > 1e-8) {
            long priced = symbols.stream().filter(s -> prices.getOrDefault(s, 0.0) > 0).count();
            if (priced > 0) {
                double per = leftover / priced;
                for (String s : symbols) {
                    double p = prices.getOrDefault(s, 0.0);
                    if (p > 0 && state.cash() >= per) {
                        state.buy(s, p, per);
                    }
                }
            }
        }
    }
}
