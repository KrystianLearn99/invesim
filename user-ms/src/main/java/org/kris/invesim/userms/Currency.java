package org.kris.invesim.userms;

public enum Currency {
    PLN("Polski złoty", "zł"),
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    GBP("British Pound", "£");

    private final String displayName;
    private final String symbol;

    Currency(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }
}