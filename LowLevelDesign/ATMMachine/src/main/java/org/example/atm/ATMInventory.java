package org.example.atm;

import org.example.enums.CashType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ATMInventory {

    private final Map<CashType, Integer> cashInventory;

    public ATMInventory() {
        this.cashInventory = new HashMap<>();
        initializeinventory();
    }

    private void initializeinventory(){
        cashInventory.put(CashType.BILL_100, 10);
        cashInventory.put(CashType.BILL_50, 10);
        cashInventory.put(CashType.BILL_20, 20);
        cashInventory.put(CashType.BILL_10, 30);
        cashInventory.put(CashType.BILL_5, 20);
        cashInventory.put(CashType.BILL_1, 50);
    }

    public synchronized BigDecimal getTotalCash(){
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<CashType, Integer> entry : cashInventory.entrySet()) {
            BigDecimal cashValue = entry.getKey().getValue();   // assume CashType stores BigDecimal
            BigDecimal count = BigDecimal.valueOf(entry.getValue());
            total = total.add(cashValue.multiply(count));
        }
        return total;
    };

    // Check if ATM has sufficient cash for a withdrawal
    public synchronized boolean hasSufficientCash(BigDecimal amount) {
        return getTotalCash().compareTo(amount) >= 0;
    };

    // Dispense cash for a withdrawal
    public synchronized Map<CashType, Integer> dispenseCash(BigDecimal amount) {
        if (!hasSufficientCash(amount)) {
            return null;
        }
        Map<CashType, Integer> dispensedCash = new HashMap<>();
        BigDecimal remainingAmount = amount;

        // Ensure largest to smallest denomination
        List<CashType> denominations = Arrays.stream(CashType.values())
                .sorted(Comparator.comparing(CashType::getValue).reversed())
                .toList();

        // Dispense from largest denomination to smallest
        for (CashType cashType : denominations) {
            BigDecimal denomValue = cashType.getValue();
            int availableNotes = cashInventory.getOrDefault(cashType, 0);

            // remainingAmount / denomValue → how many notes ideally needed
            int neededNotes = remainingAmount.divide(denomValue, 0, RoundingMode.DOWN).intValue();
            int count = Math.min(neededNotes, availableNotes);

            if (count > 0) {
                dispensedCash.put(cashType, count);
                remainingAmount = remainingAmount.subtract(denomValue.multiply(BigDecimal.valueOf(count)));
                cashInventory.put(cashType, availableNotes - count);
            }
        }
        // If we couldn't make exact change
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Rollback the transaction
            for (Map.Entry<CashType, Integer> entry : dispensedCash.entrySet()) {
                cashInventory.put(entry.getKey(),
                        cashInventory.get(entry.getKey()) + entry.getValue());
            }
            return null;
        }
        return dispensedCash;
    }

    // Add cash to inventory (for maintenance/refill)
    public synchronized void addCash(CashType cashType, int count) {
        cashInventory.put(cashType, cashInventory.get(cashType) + count);
    }
}
