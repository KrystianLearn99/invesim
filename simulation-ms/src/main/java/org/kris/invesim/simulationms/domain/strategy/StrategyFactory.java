package org.kris.invesim.simulationms.domain.strategy;

import org.kris.invesim.InvestmentStrategyType;

import java.util.List;
import java.util.Map;

public class StrategyFactory {

    public static InvestmentStrategy create(InvestmentStrategyType type,
                                            List<String> symbols,
                                            Map<String, Object> params) {
        Map<String,Object> p = params == null ? Map.of() : params;
        return switch (type) {
            case BUY_AND_HOLD -> new BuyAndHoldStrategy(symbols);
            case DCA -> {
                double amount = num(p, "amountPerStep", 0.0);
                int interval = intNum(p, "intervalSteps", 1);
                String freq = str(p, "frequency", null);
                if (freq != null) {
                    interval = switch (freq.toUpperCase()) {
                        case "WEEKLY" -> 5;
                        case "MONTHLY" -> 21;
                        default -> 1;
                    };
                }
                yield new DcaStrategy(amount, symbols, interval);
            }
            case STOP_LOSS -> {
                double sl = num(p, "stopLossPct", 0.10);
                double tp = num(p, "takeProfitPct", 0.0);
                yield new StopLossStrategy(sl, tp);
            }
        };
    }

    private static double num(Map<String,Object> p, String key, double def){
        Object v = p.get(key);
        if (v instanceof Number n) return n.doubleValue();
        if (v instanceof String s) try { return Double.parseDouble(s); } catch (Exception ignore){}
        return def;
    }
    private static int intNum(Map<String,Object> p, String key, int def){
        Object v = p.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) try { return Integer.parseInt(s); } catch (Exception ignore){}
        return def;
    }
    private static String str(Map<String,Object> p, String key, String def){
        Object v = p.get(key);
        return v == null ? def : v.toString();
    }
}
