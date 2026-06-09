package com.tradesphere.model;

/**
 * Snapshot of a user's wallet/cash position.
 * Derived view over User + Portfolio data.
 */
public class Wallet {

    private String userId;
    private double totalBalance;       // initial endowment
    private double currentCash;        // available to trade
    private double investedAmount;     // locked in holdings
    private double totalProfitOrLoss;  // realised + unrealised

    public Wallet() {}

    public Wallet(String userId, double currentCash, double investedAmount, double totalProfitOrLoss) {
        this.userId = userId;
        this.currentCash = currentCash;
        this.investedAmount = investedAmount;
        this.totalProfitOrLoss = totalProfitOrLoss;
        this.totalBalance = currentCash + investedAmount;
    }

    // ─── Computed ──────────────────────────────────────────────────────────────

    public double getPortfolioValue()  { return currentCash + investedAmount; }
    public double getNetWorth()        { return currentCash + investedAmount + totalProfitOrLoss; }
    public double getUsagePercent()    { return totalBalance > 0 ? (investedAmount / totalBalance) * 100 : 0; }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getTotalBalance() { return totalBalance; }
    public void setTotalBalance(double totalBalance) { this.totalBalance = totalBalance; }

    public double getCurrentCash() { return currentCash; }
    public void setCurrentCash(double currentCash) { this.currentCash = currentCash; }

    public double getInvestedAmount() { return investedAmount; }
    public void setInvestedAmount(double investedAmount) { this.investedAmount = investedAmount; }

    public double getTotalProfitOrLoss() { return totalProfitOrLoss; }
    public void setTotalProfitOrLoss(double totalProfitOrLoss) { this.totalProfitOrLoss = totalProfitOrLoss; }
}
