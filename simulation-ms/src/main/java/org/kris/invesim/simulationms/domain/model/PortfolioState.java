package org.kris.invesim.simulationms.domain.model;

import java.util.HashMap;
import java.util.Map;

public class PortfolioState {
    private double cash;
    private final Map<String, Double> qty = new HashMap<>();

    public PortfolioState(double initialCash, Map<String, Double> initialPositions) {
        this.cash = initialCash;
        if (initialPositions != null) qty.putAll(initialPositions);
    }

    public double cash() { return cash; }
    public double position(String symbol) { return qty.getOrDefault(symbol, 0.0); }

    public void buy(String symbol, double price, double cashAmount) {
        if (price <= 0 || cashAmount <= 0 || cashAmount > cash) return;
        double q = cashAmount / price;
        qty.merge(symbol, q, Double::sum);
        cash -= cashAmount;
    }

    public void sellAll(String symbol, double price) {
        if (price <= 0) return;
        double q = qty.getOrDefault(symbol, 0.0);
        if (q <= 0) return;
        cash += q * price;
        qty.put(symbol, 0.0);
    }

    public double value(Map<String, Double> prices) {
        return cash + qty.entrySet().stream()
                .mapToDouble(e -> e.getValue() * prices.getOrDefault(e.getKey(), 0.0))
                .sum();
    }
}
