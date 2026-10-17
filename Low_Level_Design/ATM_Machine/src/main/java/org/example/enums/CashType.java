package org.example.enums;

import java.math.BigDecimal;

public enum CashType {
    BILL_100(new BigDecimal(100)),
    BILL_50(new BigDecimal(50)),
    BILL_20(new BigDecimal(20)),
    BILL_10(new BigDecimal(10)),
    BILL_5(new BigDecimal(5)),
    BILL_1(new BigDecimal(1));


    public final BigDecimal value;
    CashType(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }
}
