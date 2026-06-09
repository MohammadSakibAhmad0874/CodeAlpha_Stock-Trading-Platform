package com.tradesphere.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user's stock portfolio.
 * Tracks holdings (symbol → Holding), total investment, and current value.
 */
public class Portfolio {

    /** Inner class representing a single stock holding */
    public static class Holding {
        private String symbol;
        private String companyName;
        private int quantity;
        private double avgBuyPrice;
        private double currentPrice;

        public Holding() {}

        public Holding(String symbol, String companyName, int quantity, double avgBuyPrice) {
            this.symbol = symbol;
            this.companyName = companyName;
            this.quantity = quantity;
            this.avgBuyPrice = avgBuyPrice;
            this.currentPrice = avgBuyPrice;
        }

        public double getInvestedAmount()  { return quantity * avgBuyPrice; }
        public double getCurrentValue()    { return quantity * currentPrice; }
        public double getProfitOrLoss()    { return getCurrentValue() - getInvestedAmount(); }
        public double getReturnPct() {
            return avgBuyPrice == 0 ? 0 : ((currentPrice - avgBuyPrice) / avgBuyPrice) * 100.0;
        }

        // Getters & Setters
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getAvgBuyPrice() { return avgBuyPrice; }
        public void setAvgBuyPrice(double avgBuyPrice) { this.avgBuyPrice = avgBuyPrice; }
        public double getCurrentPrice() { return currentPrice; }
        public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    }

    // ─── Portfolio Fields ──────────────────────────────────────────────────────

    private String userId;
    private Map<String, Holding> holdings = new HashMap<>(); // symbol → Holding

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Portfolio() {}
    public Portfolio(String userId) { this.userId = userId; }

    // ─── Business Logic ───────────────────────────────────────────────────────

    /**
     * Add a buy order to holdings (updates avg price on additional buys).
     */
    public void addHolding(String symbol, String companyName, int qty, double price) {
        if (holdings.containsKey(symbol)) {
            Holding h = holdings.get(symbol);
            double totalCost = (h.quantity * h.avgBuyPrice) + (qty * price);
            h.quantity += qty;
            h.avgBuyPrice = totalCost / h.quantity;
        } else {
            holdings.put(symbol, new Holding(symbol, companyName, qty, price));
        }
    }

    /**
     * Reduce holding on sell. Returns false if insufficient shares.
     */
    public boolean reduceHolding(String symbol, int qty) {
        Holding h = holdings.get(symbol);
        if (h == null || h.quantity < qty) return false;
        h.quantity -= qty;
        if (h.quantity == 0) holdings.remove(symbol);
        return true;
    }

    public boolean hasHolding(String symbol) {
        return holdings.containsKey(symbol) && holdings.get(symbol).quantity > 0;
    }

    public int getQuantityOwned(String symbol) {
        Holding h = holdings.get(symbol);
        return h != null ? h.quantity : 0;
    }

    public double getAvgBuyPrice(String symbol) {
        Holding h = holdings.get(symbol);
        return h != null ? h.avgBuyPrice : 0;
    }

    public void updateCurrentPrice(String symbol, double price) {
        Holding h = holdings.get(symbol);
        if (h != null) h.currentPrice = price;
    }

    public double getTotalInvested() {
        return holdings.values().stream().mapToDouble(Holding::getInvestedAmount).sum();
    }

    public double getTotalCurrentValue() {
        return holdings.values().stream().mapToDouble(Holding::getCurrentValue).sum();
    }

    public double getTotalProfitOrLoss() {
        return getTotalCurrentValue() - getTotalInvested();
    }

    public double getOverallReturnPct() {
        double invested = getTotalInvested();
        return invested == 0 ? 0 : (getTotalProfitOrLoss() / invested) * 100.0;
    }

    public int getHoldingCount() { return holdings.size(); }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Map<String, Holding> getHoldings() { return holdings; }
    public void setHoldings(Map<String, Holding> holdings) { this.holdings = holdings; }
}
