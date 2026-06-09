package com.tradesphere.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A single activity log entry. Records user actions for audit trail.
 */
public class ActivityLog {

    public enum Action {
        LOGIN, LOGOUT, BUY_ORDER, SELL_ORDER,
        PROFILE_UPDATE, THEME_CHANGE, ALERT_SET,
        ALERT_TRIGGERED, REPORT_EXPORTED, PASSWORD_CHANGED
    }

    private String id;
    private String userId;
    private Action action;
    private String description;
    private String timestamp;
    private String metadata;   // JSON string for extra context

    public ActivityLog() {}

    public ActivityLog(String id, String userId, Action action, String description) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.description = description;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getActionIcon() {
        return switch (action) {
            case LOGIN          -> "🔑";
            case LOGOUT         -> "🚪";
            case BUY_ORDER      -> "📈";
            case SELL_ORDER     -> "📉";
            case PROFILE_UPDATE -> "👤";
            case THEME_CHANGE   -> "🎨";
            case ALERT_SET      -> "🔔";
            case ALERT_TRIGGERED-> "⚠️";
            case REPORT_EXPORTED-> "📄";
            case PASSWORD_CHANGED-> "🔒";
        };
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s – %s", timestamp, getActionIcon(), action, description);
    }
}
