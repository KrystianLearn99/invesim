package org.kris.invesim.simulationms.domain.strategy;

import org.kris.invesim.simulationms.domain.model.PortfolioState;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class BuyAndHoldStrategy implements InvestmentStrategy {
    private final List<String> symbols;
    private final Map<String, Double> weights;
    private boolean bought = false;

    public BuyAndHoldStrategy(List<String> symbols) {
        this(symbols, null);
    }

    public BuyAndHoldStrategy(List<String> symbols, Map<String, Double> weights) {
        this.symbols = symbols;
        this.weights = weights;
    }

    @Override
    public void onStart(PortfolioState state, StrategyContext ctx) {
        if (symbols == null || symbols.isEmpty()) return;
        double cash = state.cash();
        if (cash <= 0) return;

        Map<String, Double> startPrices = ctx.startPrices();
        if (startPrices == null || startPrices.isEmpty()) return;

        if (weights == null || weights.isEmpty()) {
            double per = cash / symbols.size();
            for (String s : symbols) {
                double p = startPrices.getOrDefault(s, 0.0);
                if (p > 0) {
                    state.buy(s, p, per);
                }
            }
        } else {
            double sumW = weights.values().stream().mapToDouble(Double::doubleValue).sum();
            if (sumW > 0) {
                for (String s : symbols) {
                    double w = weights.getOrDefault(s, 0.0) / sumW;
                    double p = startPrices.getOrDefault(s, 0.0);
                    double alloc = cash * w;
                    if (w > 0 && p > 0) {
                        state.buy(s, p, alloc);
                    }
                }
            }
        }

        bought = true;
    }

    @Override
    public void onStep(int stepIndex, LocalDate date, Map<String, Double> prices,
                       PortfolioState state, StrategyContext ctx) {
    }

    @Override
    public void onFinish(PortfolioState state, StrategyContext ctx) {
        // no-op
    }
}
