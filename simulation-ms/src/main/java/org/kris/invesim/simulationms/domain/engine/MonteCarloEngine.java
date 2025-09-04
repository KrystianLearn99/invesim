package org.kris.invesim.simulationms.domain.engine;

import org.kris.invesim.DailyPriceDto;
import org.kris.invesim.StockDataDto;
import org.kris.invesim.simulationms.domain.model.PortfolioState;
import org.kris.invesim.simulationms.domain.strategy.InvestmentStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class MonteCarloEngine {

    public static class Result {
        public final double[] finals;
        public final List<Double> sample;
        public final List<Double> navSample;
        public Result(double[] finals, List<Double> sample, List<Double> navSample) {
            this.finals = finals; this.sample = sample; this.navSample = navSample;
        }
    }

    public Result runSingleAsset(
            StockDataDto stock,
            InvestmentStrategy investmentStrategy,
            int simulations,
            int steps,
            double initialCash,
            double initialQty,
            String symbol,
            long seed,
            String bar
    ) {
        Timeframe tf = Timeframe.fromBar(bar);
        Params p = estimate(stock, tf);

        Random rnd = new Random(seed);
        double[] finals = new double[simulations];
        List<Double> samplePath = new ArrayList<>();
        List<Double> navSample = new ArrayList<>();

        var ctx = new InvestmentStrategy.StrategyContext(tf.dtYears(), Map.of(symbol, p.s0));

        for (int sim = 0; sim < simulations; sim++) {
            double S = p.s0;
            Map<String, Double> prices = Map.of(symbol, S);
            PortfolioState state = new PortfolioState(initialCash, Map.of(symbol, initialQty));
            investmentStrategy.onStart(state, ctx);

            for (int t = 0; t < steps; t++) {
                double z = rnd.nextGaussian();
                S = stepGBM(S, p.muAnn, p.sigmaAnn, ctx.dtYears(), z);
                prices = Map.of(symbol, S);

                investmentStrategy.onStep(t, LocalDate.now().plusDays(t), prices, state, ctx);

                if (sim == 0) {
                    samplePath.add(S);
                    navSample.add(state.value(prices));
                }
            }
            investmentStrategy.onFinish(state, ctx);
            finals[sim] = state.value(prices);
        }
        return new Result(finals, samplePath, navSample);
    }

    private record Params(double s0, double muAnn, double sigmaAnn) {}

    private Params estimate(StockDataDto stock, Timeframe tf) {
        List<DailyPriceDto> series = resampleToTimeframe(stock.getPrices(), tf);
        if (series.size() < 2) return new Params(100.0, 0.05, 0.20);

        double[] logR = new double[series.size() - 1];
        for (int i = 1; i < series.size(); i++) {
            double prev = d(series.get(i - 1).getClose());
            double curr = d(series.get(i).getClose());
            logR[i - 1] = Math.log(Math.max(1e-12, curr / Math.max(1e-12, prev)));
        }
        double meanStep = Arrays.stream(logR).average().orElse(0.0);

        double var = 0.0;
        for (double r : logR) { double dd = r - meanStep; var += dd * dd; }
        var /= Math.max(1, logR.length - 1);
        double sigmaStep = Math.sqrt(var);

        int k = tf.stepsPerYear();
        double muAnn = meanStep * k;
        double sigmaAnn = sigmaStep * Math.sqrt(k);

        double s0 = d(series.get(series.size() - 1).getClose());
        return new Params(s0, muAnn, sigmaAnn);
    }

    private List<DailyPriceDto> resampleToTimeframe(List<DailyPriceDto> px, Timeframe tf) {
        if (px == null || px.isEmpty()) return List.of();
        if (tf == Timeframe.DAILY) return px;

        if (tf == Timeframe.WEEKLY) {
            List<DailyPriceDto> out = new ArrayList<>();
            for (int i = 0; i < px.size(); i += 5) {
                out.add(px.get(Math.min(i + 4, px.size() - 1)));
            }
            return out;
        }

        Map<String, DailyPriceDto> lastOfMonth = new LinkedHashMap<>();
        for (DailyPriceDto d : px) {
            String ym = d.getDate().getYear() + "-" + String.format("%02d", d.getDate().getMonthValue());
            DailyPriceDto prev = lastOfMonth.get(ym);
            if (prev == null || d.getDate().isAfter(prev.getDate())) {
                lastOfMonth.put(ym, d);
            }
        }
        return lastOfMonth.values().stream().collect(Collectors.toList());
    }

    private double stepGBM(double S, double muAnn, double sigmaAnn, double dtYears, double z) {
        double drift = (muAnn - 0.5 * sigmaAnn * sigmaAnn) * dtYears;
        double shock = sigmaAnn * Math.sqrt(dtYears) * z;
        return S * Math.exp(drift + shock);
    }

    private static double d(BigDecimal bd) { return bd == null ? 0.0 : bd.doubleValue(); }
}
