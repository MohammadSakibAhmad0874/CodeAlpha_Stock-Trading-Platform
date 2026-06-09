package com.tradesphere.controller;

import com.tradesphere.database.DatabaseManager;
import com.tradesphere.model.*;
import com.tradesphere.services.*;

import java.util.UUID;

/**
 * Handles buy and sell trade execution with full validation.
 */
public class TradeController {

    private static TradeController instance;
    private TradeController() {}
    public static TradeController getInstance() {
        if (instance == null) instance = new TradeController();
        return instance;
    }

    public record TradeResult(boolean success, String message, double amount) {}

    // ─── BUY ─────────────────────────────────────────────────────────────────

    public TradeResult buy(String symbol, int quantity) {
        AppController app  = AppController.getInstance();
        User user          = app.getCurrentUser();
        Portfolio portfolio = app.getPortfolio();

        if (user == null || portfolio == null)
            return new TradeResult(false, "Not logged in.", 0);
        if (quantity <= 0)
            return new TradeResult(false, "Quantity must be greater than zero.", 0);

        Stock stock = MarketSimulator.getInstance().getStock(symbol);
        if (stock == null)
            return new TradeResult(false, "Stock not found: " + symbol, 0);

        double cost = stock.getCurrentPrice() * quantity;
        if (!user.canAfford(cost))
            return new TradeResult(false,
                String.format("Insufficient funds. Need ₹%.2f, Available ₹%.2f", cost, user.getWalletBalance()), 0);

        // Execute
        user.debit(cost);
        portfolio.addHolding(symbol, stock.getCompanyName(), quantity, stock.getCurrentPrice());

        // Record transaction
        String txId = UUID.randomUUID().toString();
        Transaction tx = Transaction.buy(txId, user.getId(), symbol, stock.getCompanyName(),
                                          quantity, stock.getCurrentPrice());
        app.addTransaction(tx);
        DatabaseManager.getInstance().saveTransaction(tx);
        DatabaseManager.getInstance().saveHolding(user.getId(), portfolio.getHoldings().get(symbol));
        DatabaseManager.getInstance().saveUser(user);

        user.setTotalTrades(user.getTotalTrades() + 1);
        app.logActivity(ActivityLog.Action.BUY_ORDER,
            String.format("Bought %d shares of %s @ ₹%.2f", quantity, symbol, stock.getCurrentPrice()));

        NotificationManager.getInstance().success("Order Executed",
            String.format("Bought %d × %s for ₹%.2f", quantity, symbol, cost));

        return new TradeResult(true,
            String.format("✅ Successfully bought %d shares of %s for ₹%.2f", quantity, symbol, cost), cost);
    }

    // ─── SELL ────────────────────────────────────────────────────────────────

    public TradeResult sell(String symbol, int quantity) {
        AppController app   = AppController.getInstance();
        User user           = app.getCurrentUser();
        Portfolio portfolio  = app.getPortfolio();

        if (user == null || portfolio == null)
            return new TradeResult(false, "Not logged in.", 0);
        if (quantity <= 0)
            return new TradeResult(false, "Quantity must be greater than zero.", 0);

        int owned = portfolio.getQuantityOwned(symbol);
        if (owned < quantity)
            return new TradeResult(false,
                String.format("You only own %d shares of %s.", owned, symbol), 0);

        Stock stock = MarketSimulator.getInstance().getStock(symbol);
        if (stock == null)
            return new TradeResult(false, "Stock not found: " + symbol, 0);

        double avgBuy = portfolio.getAvgBuyPrice(symbol);
        double proceeds = stock.getCurrentPrice() * quantity;
        double pl = (stock.getCurrentPrice() - avgBuy) * quantity;

        // Execute
        portfolio.reduceHolding(symbol, quantity);
        user.credit(proceeds);
        user.recordTrade(pl);

        // Persist
        String txId = UUID.randomUUID().toString();
        Transaction tx = Transaction.sell(txId, user.getId(), symbol, stock.getCompanyName(),
                                           quantity, stock.getCurrentPrice(), avgBuy);
        app.addTransaction(tx);
        DatabaseManager.getInstance().saveTransaction(tx);

        if (portfolio.hasHolding(symbol))
            DatabaseManager.getInstance().saveHolding(user.getId(), portfolio.getHoldings().get(symbol));
        else
            DatabaseManager.getInstance().deleteHolding(user.getId(), symbol);

        DatabaseManager.getInstance().saveUser(user);

        app.logActivity(ActivityLog.Action.SELL_ORDER,
            String.format("Sold %d shares of %s @ ₹%.2f | P&L: ₹%+.2f",
                quantity, symbol, stock.getCurrentPrice(), pl));

        String plLabel = pl >= 0
            ? String.format("+₹%.2f profit", pl)
            : String.format("-₹%.2f loss", Math.abs(pl));

        NotificationManager.getInstance().success("Order Executed",
            String.format("Sold %d × %s for ₹%.2f (%s)", quantity, symbol, proceeds, plLabel));

        return new TradeResult(true,
            String.format("✅ Sold %d shares of %s for ₹%.2f (%s)", quantity, symbol, proceeds, plLabel), proceeds);
    }
}
