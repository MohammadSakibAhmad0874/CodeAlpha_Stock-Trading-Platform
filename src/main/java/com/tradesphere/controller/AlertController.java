package com.tradesphere.controller;

import com.tradesphere.database.DatabaseManager;
import com.tradesphere.model.*;
import com.tradesphere.services.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Manages price alert creation, checking, and deletion.
 */
public class AlertController {

    private static AlertController instance;
    private AlertController() {}
    public static AlertController getInstance() {
        if (instance == null) instance = new AlertController();
        return instance;
    }

    public record AlertResult(boolean success, String message) {}

    public AlertResult createAlert(String symbol, String companyName,
                                    PriceAlert.Condition condition, double targetPrice) {
        AppController app = AppController.getInstance();
        if (app.getCurrentUser() == null) return new AlertResult(false, "Not logged in.");
        if (targetPrice <= 0) return new AlertResult(false, "Target price must be positive.");

        String id = UUID.randomUUID().toString();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        PriceAlert alert = new PriceAlert(id, app.getCurrentUser().getId(),
                                           symbol, companyName, condition, targetPrice, now);
        app.getAlerts().add(alert);
        DatabaseManager.getInstance().saveAlert(alert);
        app.logActivity(ActivityLog.Action.ALERT_SET,
            String.format("Alert set: %s %s ₹%.2f", symbol, condition, targetPrice));
        NotificationManager.getInstance().info("Alert Created",
            String.format("%s when %s %s ₹%.2f", symbol, symbol, alert.getConditionLabel(), targetPrice));
        return new AlertResult(true, "Alert created for " + symbol);
    }

    public void deleteAlert(String alertId) {
        AppController app = AppController.getInstance();
        app.getAlerts().removeIf(a -> a.getId().equals(alertId));
        DatabaseManager.getInstance().deleteAlert(alertId);
    }

    /** Called on every market tick to check all active alerts. */
    public void checkAlerts(List<Stock> stocks) {
        AppController app = AppController.getInstance();
        if (app.getCurrentUser() == null) return;

        for (PriceAlert alert : app.getAlerts()) {
            if (alert.getStatus() != PriceAlert.Status.ACTIVE) continue;
            Stock stock = stocks.stream()
                .filter(s -> s.getSymbol().equals(alert.getStockSymbol()))
                .findFirst().orElse(null);
            if (stock == null) continue;

            if (alert.isTriggered(stock.getCurrentPrice())) {
                alert.setStatus(PriceAlert.Status.TRIGGERED);
                alert.setTriggeredAt(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                DatabaseManager.getInstance().saveAlert(alert);
                app.logActivity(ActivityLog.Action.ALERT_TRIGGERED,
                    String.format("Alert triggered: %s %s ₹%.2f",
                        alert.getStockSymbol(), alert.getConditionLabel(), alert.getTargetPrice()));
                NotificationManager.getInstance().warning("⚠️ Price Alert",
                    String.format("%s reached ₹%.2f (target: %s ₹%.2f)",
                        alert.getStockSymbol(), stock.getCurrentPrice(),
                        alert.getConditionLabel(), alert.getTargetPrice()));
            }
        }
    }
}
