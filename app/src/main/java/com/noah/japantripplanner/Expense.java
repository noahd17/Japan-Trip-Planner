package com.noah.japantripplanner;

/**
 * A budget line item, stored in: users/{uid}/expenses/{expenseId}
 * Amounts are always stored in USD; JPY conversion happens at display time.
 */
public class Expense {

    private String id;
    private String category;
    private double amountUsd;
    private long timestamp;

    public Expense() {
    }

    public Expense(String category, double amountUsd, long timestamp) {
        this.category = category;
        this.amountUsd = amountUsd;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmountUsd() {
        return amountUsd;
    }

    public void setAmountUsd(double amountUsd) {
        this.amountUsd = amountUsd;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
