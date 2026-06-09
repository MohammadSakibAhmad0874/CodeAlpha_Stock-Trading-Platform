package com.tradesphere.controller;

import com.tradesphere.database.DatabaseManager;
import com.tradesphere.model.*;
import com.tradesphere.services.*;

import java.util.*;

/**
 * Singleton application state controller.
 * Holds the logged-in user, portfolio, and coordinates navigation.
 */
public class AppController {

    private static AppController instance;

    private User currentUser;
    private Portfolio portfolio;
    private List<Transaction> transactions = new ArrayList<>();
    private List<PriceAlert>  alerts       = new ArrayList<>();
    private List<ActivityLog> activityLogs = new ArrayList<>();
    private Set<String>       watchlist    = new HashSet<>();

    // Navigation callback (set by MainWindow)
    private Runnable onNavigate;
    private String   currentView = "DASHBOARD";

    private AppController() {}

    public static AppController getInstance() {
        if (instance == null) instance = new AppController();
        return instance;
    }

    // ─── Session Management ───────────────────────────────────────────────────

    public void login(User user) {
        this.currentUser  = user;
        this.portfolio    = new Portfolio(user.getId());
        this.transactions = DatabaseManager.getInstance().loadTransactions(user.getId());
        this.alerts       = DatabaseManager.getInstance().loadAlerts(user.getId());
        this.activityLogs = DatabaseManager.getInstance().loadLogs(user.getId());
        this.watchlist    = DatabaseManager.getInstance().loadWatchlist(user.getId());

        // Load holdings into portfolio
        Map<String, Portfolio.Holding> holdings = DatabaseManager.getInstance().loadHoldings(user.getId());
        portfolio.getHoldings().putAll(holdings);

        // Sync current prices
        syncPortfolioPrices();

        // Log
        logActivity(ActivityLog.Action.LOGIN, "User logged in: " + user.getUsername());

        // Apply saved theme
        ThemeManager.Theme theme = "LIGHT".equals(user.getTheme())
            ? ThemeManager.Theme.LIGHT : ThemeManager.Theme.DARK;
        ThemeManager.getInstance().setTheme(theme);

        user.updateLastLogin();
        DatabaseManager.getInstance().saveUser(user);
    }

    public void logout() {
        if (currentUser != null) {
            logActivity(ActivityLog.Action.LOGOUT, "User logged out: " + currentUser.getUsername());
            DatabaseManager.getInstance().saveUser(currentUser);
        }
        currentUser  = null;
        portfolio    = null;
        transactions.clear();
        alerts.clear();
        activityLogs.clear();
        watchlist.clear();
    }

    public boolean isLoggedIn() { return currentUser != null; }

    // ─── Portfolio Sync ───────────────────────────────────────────────────────

    public void syncPortfolioPrices() {
        if (portfolio == null) return;
        MarketSimulator sim = MarketSimulator.getInstance();
        for (String symbol : portfolio.getHoldings().keySet()) {
            Stock s = sim.getStock(symbol);
            if (s != null) portfolio.updateCurrentPrice(symbol, s.getCurrentPrice());
        }
    }

    // ─── Watchlist ────────────────────────────────────────────────────────────

    public void addToWatchlist(String symbol) {
        watchlist.add(symbol);
        DatabaseManager.getInstance().addToWatchlist(currentUser.getId(), symbol);
        Stock s = MarketSimulator.getInstance().getStock(symbol);
        if (s != null) s.setInWatchlist(true);
    }

    public void removeFromWatchlist(String symbol) {
        watchlist.remove(symbol);
        DatabaseManager.getInstance().removeFromWatchlist(currentUser.getId(), symbol);
        Stock s = MarketSimulator.getInstance().getStock(symbol);
        if (s != null) s.setInWatchlist(false);
    }

    public boolean isInWatchlist(String symbol) { return watchlist.contains(symbol); }
    public Set<String> getWatchlist() { return Collections.unmodifiableSet(watchlist); }

    // ─── Activity Logging ─────────────────────────────────────────────────────

    public void logActivity(ActivityLog.Action action, String description) {
        String id  = UUID.randomUUID().toString();
        ActivityLog log = new ActivityLog(id, currentUser != null ? currentUser.getId() : "system", action, description);
        activityLogs.add(0, log);
        DatabaseManager.getInstance().saveLog(log);
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    public User getCurrentUser()         { return currentUser; }
    public Portfolio getPortfolio()      { return portfolio; }
    public List<Transaction> getTransactions() { return transactions; }
    public List<PriceAlert>  getAlerts()       { return alerts;       }
    public List<ActivityLog> getActivityLogs() { return activityLogs; }
    public String getCurrentView()             { return currentView;  }

    public void setCurrentView(String view)    { this.currentView = view; }
    public void setCurrentUser(User user)      { this.currentUser = user; }

    public void addTransaction(Transaction t)  { transactions.add(0, t); }
    public void addAlert(PriceAlert a)         { alerts.add(a); }

    public List<Transaction> getRecentTransactions(int n) {
        return transactions.stream().limit(n).toList();
    }

    // Stats for leaderboard
    public double getPortfolioTotalValue() {
        if (currentUser == null || portfolio == null) return 0;
        return currentUser.getWalletBalance() + portfolio.getTotalCurrentValue();
    }
}
