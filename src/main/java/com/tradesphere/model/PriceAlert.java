package com.tradesphere.model;

/**
 * A price alert set by a user for a specific stock.
 */
public class PriceAlert {

    public enum Condition { ABOVE, BELOW }
    public enum Status { ACTIVE, TRIGGERED, DISMISSED }

    private String id;
    private String userId;
    private String stockSymbol;
    private String companyName;
    private Condition condition;   // ABOVE or BELOW
    private double targetPrice;
    private Status status;
    private String createdAt;
    private String triggeredAt;

    public PriceAlert() {}

    public PriceAlert(String id, String userId, String stockSymbol, String companyName,
                      Condition condition, double targetPrice, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.companyName = companyName;
        this.condition = condition;
        this.targetPrice = targetPrice;
        this.status = Status.ACTIVE;
        this.createdAt = createdAt;
    }

    public boolean isTriggered(double currentPrice) {
        if (status != Status.ACTIVE) return false;
        return condition == Condition.ABOVE
                ? currentPrice >= targetPrice
                : currentPrice <= targetPrice;
    }

    public String getConditionLabel() {
        return condition == Condition.ABOVE ? "≥" : "≤";
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }
    public double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(double targetPrice) { this.targetPrice = targetPrice; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(String triggeredAt) { this.triggeredAt = triggeredAt; }
}
