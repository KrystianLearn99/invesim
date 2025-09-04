package org.kris.invesim.simulationms.domain.engine;

public enum Timeframe {
    DAILY(252),
    WEEKLY(52),
    MONTHLY(12);

    private final int stepsPerYear;
    Timeframe(int s){ this.stepsPerYear = s; }
    public int stepsPerYear(){ return stepsPerYear; }
    public double dtYears(){ return 1.0 / stepsPerYear; }

    public static Timeframe fromBar(String bar){
        if (bar == null) return DAILY;
        return switch (bar.toLowerCase()) {
            case "1w", "1wk", "1week" -> WEEKLY;
            case "1m", "1mo", "1month" -> MONTHLY;
            default -> DAILY;
        };
    }
}
