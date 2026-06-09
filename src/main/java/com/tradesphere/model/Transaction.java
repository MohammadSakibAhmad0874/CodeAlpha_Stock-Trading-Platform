package com.tradesphere.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single buy or sell transaction.
 * Immutable record of a completed trade.
 */
public class Transaction {

    public enum Type { BUY, SELL }

    private String id;
    private String userId;
    private String stockSymbol;
    private String companyName;
    private Type type;
    private int quantity;
    private double pricePerShare;
    private double totalAmount;
    private double profitOrLoss;   // 0 for buys; calculated on sell
    private String timestamp;
    private String notes;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Transaction() {}

    /** Factory method for a BUY transaction */
    public static Transaction buy(String id, String userId, String symbol,
                                   String companyName, int qty, double price) {
        Transaction t = new Transaction();
        t.id = id;
        t.userId = userId;
        t.stockSymbol = symbol;
        t.companyName = companyName;
        t.type = Type.BUY;
        t.quantity = qty;
        t.pricePerShare = price;
        t.totalAmount = qty * price;
        t.profitOrLoss = 0;
        t.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return t;
    }

    /** Factory method for a SELL transaction */
    public static Transaction sell(String id, String userId, String symbol,
                                    String companyName, int qty, double price,
                                    double avgBuyPrice) {
        Transaction t = new Transaction();
        t.id = id;
        t.userId = userId;
        t.stockSymbol = symbol;
        t.companyName = companyName;
        t.type = Type.SELL;
        t.quantity = qty;
        t.pricePerShare = price;
        t.totalAmount = qty * price;
        t.profitOrLoss = (price - avgBuyPrice) * qty;
        t.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return t;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    public boolean isProfitable() { return profitOrLoss > 0; }
    public boolean isBuy()  { return type == Type.BUY;  }
    public boolean isSell() { return type == Type.SELL; }

    public String getTypeLabel() { return type == Type.BUY ? "BUY" : "SELL"; }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPricePerShare() { return pricePerShare; }
    public void setPricePerShare(double pricePerShare) { this.pricePerShare = pricePerShare; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getProfitOrLoss() { return profitOrLoss; }
    public void setProfitOrLoss(double profitOrLoss) { this.profitOrLoss = profitOrLoss; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("[%s] %s %d x %s @ ₹%.2f = ₹%.2f",
                timestamp, type, quantity, stockSymbol, pricePerShare, totalAmount);
    }
}
