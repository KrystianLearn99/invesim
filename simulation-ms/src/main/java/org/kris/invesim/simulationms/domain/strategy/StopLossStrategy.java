package org.kris.invesim.simulationms.domain.strategy;

import org.kris.invesim.simulationms.domain.model.PortfolioState;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class StopLossStrategy implements InvestmentStrategy {
    private final double stopLossPct;
    private final double takeProfitPct;
    private final Map<String, Double> peak = new HashMap<>();

    public StopLossStrategy(double stopLossPct, double takeProfitPct) {
        this.stopLossPct = stopLossPct;
        this.takeProfitPct = takeProfitPct;
    }

    @Override
    public void onStep(int stepIndex, LocalDate date, Map<String, Double> prices, PortfolioState state, StrategyContext ctx) {
        for (var e : prices.entrySet()) {
            String sym = e.getKey();
            double price = e.getValue();
            peak.merge(sym, price, Math::max);
            double p = peak.get(sym);
            if (p <= 0) continue;

            if (state.position(sym) > 0) {
                if (stopLossPct > 0 && price <= p * (1.0 - stopLossPct)) {
                    state.sellAll(sym, price);
                } else if (takeProfitPct > 0 && price >= p * (1.0 + takeProfitPct)) {
                    state.sellAll(sym, price);
                }
            }
        }
    }
}
