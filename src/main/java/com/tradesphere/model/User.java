package com.tradesphere.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a TradeSphere Pro user account.
 * Encapsulates personal info, authentication credentials, and preferences.
 */
public class User {

    private String id;
    private String fullName;
    private String email;
    private String username;
    private String passwordHash;
    private double walletBalance;
    private double initialBalance;
    private String theme;          // "DARK" or "LIGHT"
    private String currency;       // "INR", "USD"
    private boolean notificationsEnabled;
    private boolean rememberMe;
    private String createdAt;
    private String lastLogin;
    private int totalTrades;
    private double totalProfit;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public User() {}

    public User(String id, String fullName, String email, String username, String passwordHash) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.walletBalance = 100_000.0;   // ₹1,00,000 virtual cash
        this.initialBalance = 100_000.0;
        this.theme = "DARK";
        this.currency = "INR";
        this.notificationsEnabled = true;
        this.rememberMe = false;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.lastLogin = this.createdAt;
        this.totalTrades = 0;
        this.totalProfit = 0.0;
    }

    // ─── Business Logic ───────────────────────────────────────────────────────

    public boolean canAfford(double amount) {
        return walletBalance >= amount;
    }

    public void debit(double amount) {
        if (!canAfford(amount)) throw new IllegalStateException("Insufficient funds");
        walletBalance -= amount;
    }

    public void credit(double amount) {
        walletBalance += amount;
    }

    public double getTotalReturn() {
        return walletBalance - initialBalance;
    }

    public double getTotalReturnPct() {
        return (getTotalReturn() / initialBalance) * 100.0;
    }

    public void recordTrade(double profitOrLoss) {
        totalTrades++;
        totalProfit += profitOrLoss;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }

    public double getInitialBalance() { return initialBalance; }
    public void setInitialBalance(double initialBalance) { this.initialBalance = initialBalance; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public boolean isRememberMe() { return rememberMe; }
    public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public int getTotalTrades() { return totalTrades; }
    public void setTotalTrades(int totalTrades) { this.totalTrades = totalTrades; }

    public double getTotalProfit() { return totalProfit; }
    public void setTotalProfit(double totalProfit) { this.totalProfit = totalProfit; }

    @Override
    public String toString() {
        return String.format("User[%s | %s | ₹%.2f]", username, fullName, walletBalance);
    }
}
