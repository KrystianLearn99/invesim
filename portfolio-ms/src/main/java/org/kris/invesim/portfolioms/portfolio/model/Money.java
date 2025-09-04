package org.kris.invesim.portfolioms.portfolio.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
    private Money() {}

    public static final RoundingMode RM = RoundingMode.HALF_UP;
    public static final int SCALE_CASH = 4;
    public static final int SCALE_PRICE = 6;
    public static final int SCALE_QTY = 10;

    public static BigDecimal cash(BigDecimal v)   { return v.setScale(SCALE_CASH, RM); }
    public static BigDecimal price(BigDecimal v)  { return v.setScale(SCALE_PRICE, RM); }
    public static BigDecimal qty(BigDecimal v)    { return v.setScale(SCALE_QTY, RM); }

    public static BigDecimal zero() { return BigDecimal.ZERO.setScale(SCALE_CASH, RM); }

    public static BigDecimal safe(BigDecimal v) {
        return v == null ? zero() : v;
    }
}
