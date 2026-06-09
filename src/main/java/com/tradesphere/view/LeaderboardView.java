package com.tradesphere.view;

import com.tradesphere.controller.AppController;
import com.tradesphere.database.DatabaseManager;
import com.tradesphere.model.User;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.List;

/**
 * Trader leaderboard ranked by total portfolio value.
 */
public class LeaderboardView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);

    public LeaderboardView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");
        buildContent();
        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#0d0f14; -fx-background:#0d0f14;");
    }

    private void buildContent() {
        VBox header = new VBox(4);
        Label title = new Label("🏆 Trader Leaderboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill:#e8eaed;");
        Label sub = new Label("Rankings based on total portfolio value across all traders");
        sub.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");
        header.getChildren().addAll(title, sub);

        VBox leaderboard = buildLeaderboardTable();
        content.getChildren().addAll(header, leaderboard);
    }

    private VBox buildLeaderboardTable() {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(0, 0, 16, 0));

        // Table header
        HBox hdr = new HBox(0);
        hdr.setPadding(new Insets(14, 20, 14, 20));
        hdr.setStyle("-fx-background-color:#111318; -fx-background-radius:14 14 0 0;");
        hdr.setAlignment(Pos.CENTER_LEFT);
        for (String[] h : new String[][]{{"#", "50"},{"TRADER","200"},{"BALANCE","140"},{"INVESTED","140"},{"P&L","130"},{"ROI","100"},{"TRADES","80"}}) {
            Label l = new Label(h[0]);
            l.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px; -fx-font-weight:bold; -fx-min-width:" + h[1] + ";");
            hdr.getChildren().add(l);
        }
        card.getChildren().add(hdr);

        List<User> users = DatabaseManager.getInstance().getAllUsers();
        AppController app = AppController.getInstance();

        String[] rankColors = {"#f59e0b", "#94a3b8", "#cd7f32"};
        String[] rankIcons  = {"🥇", "🥈", "🥉"};

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            boolean isMe = app.getCurrentUser() != null &&
                           u.getUsername().equals(app.getCurrentUser().getUsername());

            HBox row = new HBox(0);
            row.setPadding(new Insets(14, 20, 14, 20));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle((i % 2 == 0 ? "-fx-background-color:rgba(255,255,255,0.02);" : "") +
                (isMe ? "-fx-background-color:rgba(59,130,246,0.1); -fx-border-color:transparent transparent rgba(59,130,246,0.3) transparent; -fx-border-width:0 0 1 0;" : ""));

            // Rank
            String rankStr = i < 3 ? rankIcons[i] : String.valueOf(i + 1);
            String rankStyle = i < 3
                ? "-fx-text-fill:" + rankColors[i] + "; -fx-font-size:16px; -fx-font-weight:bold; -fx-min-width:50;"
                : "-fx-text-fill:#64748b; -fx-font-size:14px; -fx-min-width:50;";
            Label rank = new Label(rankStr); rank.setStyle(rankStyle);

            // Username
            Label nameLabel = new Label((isMe ? "★ " : "") + u.getFullName());
            nameLabel.setStyle("-fx-text-fill:" + (isMe ? "#3b82f6" : "#e8eaed") +
                "; -fx-font-weight:" + (isMe ? "bold" : "normal") + "; -fx-font-size:13px; -fx-min-width:200;");

            // Wallet balance
            Label bal = new Label(String.format("₹%.0f", u.getWalletBalance()));
            bal.setStyle("-fx-text-fill:#e8eaed; -fx-font-size:13px; -fx-min-width:140;");

            // Total profit
            double pl = u.getTotalProfit();
            Label plLabel = new Label(String.format("₹%+.0f", pl));
            plLabel.setStyle("-fx-text-fill:" + (pl >= 0 ? "#22c55e" : "#ef4444") +
                "; -fx-font-weight:bold; -fx-font-size:13px; -fx-min-width:130;");

            // ROI
            double roi = u.getTotalReturnPct();
            Label roiLabel = new Label(String.format("%+.2f%%", roi));
            roiLabel.setStyle("-fx-background-color:" + (roi >= 0 ? "rgba(34,197,94,0.12)" : "rgba(239,68,68,0.12)") +
                "; -fx-background-radius:5; -fx-padding:3 8; -fx-text-fill:" +
                (roi >= 0 ? "#22c55e" : "#ef4444") + "; -fx-font-size:11px; -fx-font-weight:bold; -fx-min-width:100;");

            // Trades
            Label trades = new Label(String.valueOf(u.getTotalTrades()));
            trades.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px; -fx-min-width:80;");

            // Invested (placeholder)
            Label invested = new Label("₹–");
            invested.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px; -fx-min-width:140;");

            row.getChildren().addAll(rank, nameLabel, bal, invested, plLabel, roiLabel, trades);
            card.getChildren().add(row);
        }

        if (users.isEmpty()) {
            Label empty = new Label("No traders registered yet.");
            empty.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px; -fx-padding:24;");
            card.getChildren().add(empty);
        }

        return card;
    }

    public Parent getRoot() { return root; }
}
