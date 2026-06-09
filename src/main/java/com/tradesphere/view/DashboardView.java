package com.tradesphere.view;

import com.tradesphere.controller.AppController;
import com.tradesphere.model.*;
import com.tradesphere.services.MarketSimulator;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.util.List;

/**
 * Main dashboard with portfolio summary, market indices, watchlist, and recent trades.
 */
public class DashboardView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);

    // Live labels updated by market sim
    private Label portfolioValueLbl;
    private Label portfolioPLLbl;
    private Label cashLbl;
    private VBox  watchlistBox;

    public DashboardView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");

        buildContent();

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #0d0f14; -fx-background: #0d0f14;");

        // Subscribe to live market updates
        MarketSimulator.getInstance().addListener(stocks -> refreshLiveData(stocks));
    }

    private void buildContent() {
        // ── Header ───────────────────────────────────────────────────────────
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(2);
        AppController app = AppController.getInstance();
        String name = app.getCurrentUser() != null ? app.getCurrentUser().getFullName() : "Trader";
        Label greeting = new Label("Good day, " + name.split(" ")[0] + "! 👋");
        greeting.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        greeting.setStyle("-fx-text-fill: #e8eaed;");
        Label sub = new Label("Here's your trading overview for today.");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(greeting, sub);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label clock = new Label();
        clock.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        Timeline clockTimer = new Timeline(new KeyFrame(Duration.seconds(1), e ->
            clock.setText(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  HH:mm:ss")))));
        clockTimer.setCycleCount(Animation.INDEFINITE);
        clockTimer.play();
        header.getChildren().addAll(titleBox, sp, clock);

        // ── Top stat cards row ────────────────────────────────────────────────
        HBox statsRow = new HBox(16);
        statsRow.setFillHeight(true);

        portfolioValueLbl = new Label("₹0.00");
        portfolioValueLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        portfolioValueLbl.setStyle("-fx-text-fill: #e8eaed;");

        portfolioPLLbl = new Label("₹0.00 (0.00%)");
        portfolioPLLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");

        cashLbl = new Label("₹0.00");
        cashLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        cashLbl.setStyle("-fx-text-fill: #e8eaed;");

        VBox portfolioCard = statCard("💼 Portfolio Value", portfolioValueLbl, portfolioPLLbl, "#1e3a5f");
        VBox cashCard      = statCard("💵 Available Cash",  cashLbl,
            labelOf("Ready to invest"), "#1a3320");

        // Holdings count
        int holdCount = app.getPortfolio() != null ? app.getPortfolio().getHoldingCount() : 0;
        Label holdLbl = new Label(String.valueOf(holdCount));
        holdLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        holdLbl.setStyle("-fx-text-fill: #e8eaed;");
        VBox holdCard = statCard("📊 Holdings", holdLbl, labelOf("Stocks owned"), "#1e1a3f");

        int tradeCount = app.getTransactions().size();
        Label tradeLbl = new Label(String.valueOf(tradeCount));
        tradeLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        tradeLbl.setStyle("-fx-text-fill: #e8eaed;");
        VBox tradeCard = statCard("🔄 Total Trades", tradeLbl, labelOf("All time"), "#1f1a14");

        HBox.setHgrow(portfolioCard, Priority.ALWAYS);
        HBox.setHgrow(cashCard,      Priority.ALWAYS);
        HBox.setHgrow(holdCard,      Priority.ALWAYS);
        HBox.setHgrow(tradeCard,     Priority.ALWAYS);
        statsRow.getChildren().addAll(portfolioCard, cashCard, holdCard, tradeCard);

        // ── Middle row: Market Indices + Watchlist ─────────────────────────
        HBox midRow = new HBox(16);

        VBox indicesCard  = buildMarketIndices();
        VBox watchlistCard = buildWatchlist();
        HBox.setHgrow(indicesCard,   Priority.ALWAYS);
        HBox.setHgrow(watchlistCard, Priority.ALWAYS);
        midRow.getChildren().addAll(indicesCard, watchlistCard);

        // ── Recent trades ──────────────────────────────────────────────────
        VBox recentTrades = buildRecentTrades();

        content.getChildren().addAll(header, statsRow, midRow, recentTrades);

        // Initial data load
        refreshLiveData(MarketSimulator.getInstance().getStockList());
    }

    // ─── Market Indices ───────────────────────────────────────────────────────

    private VBox buildMarketIndices() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label title = new Label("📈 Market Indices");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setStyle("-fx-text-fill: #e8eaed;");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(12);

        String[][] indices = {
            {"NIFTY 50",  "22,450.65", "+0.82%"},
            {"SENSEX",    "73,920.34", "+0.75%"},
            {"NASDAQ",    "15,680.12", "-0.34%"},
            {"S&P 500",   "4,980.56",  "+0.41%"},
            {"DOW JONES", "38,240.80", "+0.22%"},
            {"FTSE 100",  "7,820.45",  "-0.15%"},
        };

        for (int i = 0; i < indices.length; i++) {
            String[] idx = indices[i];
            boolean pos = idx[2].startsWith("+");

            Label name = new Label(idx[0]);
            name.setStyle("-fx-text-fill: #94a3b8; -fx-font-size:12px;");

            Label val = new Label(idx[1]);
            val.setStyle("-fx-text-fill: #e8eaed; -fx-font-weight:bold; -fx-font-size:13px;");

            Label chg = new Label(idx[2]);
            chg.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" +
                (pos ? "#22c55e" : "#ef4444") + ";");
            chg.setStyle("-fx-background-color:" + (pos ? "rgba(34,197,94,0.12)" : "rgba(239,68,68,0.12)") +
                "; -fx-background-radius:5; -fx-padding:2 8; -fx-text-fill:" +
                (pos ? "#22c55e" : "#ef4444") + "; -fx-font-size:11px; -fx-font-weight:bold;");

            grid.add(name, 0, i);
            grid.add(val,  1, i);
            grid.add(chg,  2, i);

            ColumnConstraints c0 = new ColumnConstraints(130);
            ColumnConstraints c1 = new ColumnConstraints(100);
            ColumnConstraints c2 = new ColumnConstraints(80);
            grid.getColumnConstraints().setAll(c0, c1, c2);
        }

        card.getChildren().addAll(title, grid);
        return card;
    }

    // ─── Watchlist ────────────────────────────────────────────────────────────

    private VBox buildWatchlist() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("⭐ Watchlist");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setStyle("-fx-text-fill: #e8eaed;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label hint = new Label("Auto-updated");
        hint.setStyle("-fx-text-fill:#334155; -fx-font-size:10px;");
        header.getChildren().addAll(title, sp, hint);

        watchlistBox = new VBox(8);
        refreshWatchlist();

        card.getChildren().addAll(header, watchlistBox);
        return card;
    }

    private void refreshWatchlist() {
        watchlistBox.getChildren().clear();
        List<Stock> stocks = MarketSimulator.getInstance().getStockList();
        // Show first 8 or watchlist items
        AppController app = AppController.getInstance();
        List<Stock> toShow = stocks.stream()
            .filter(s -> app.getWatchlist().isEmpty() || app.isInWatchlist(s.getSymbol()))
            .limit(8).toList();
        if (toShow.isEmpty()) toShow = stocks.stream().limit(8).toList();

        for (Stock s : toShow) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 8, 6, 8));
            row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius:8;");

            Label sym = new Label(s.getSymbol());
            sym.setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold; -fx-font-size:12px; -fx-min-width:60;");
            Label name = new Label(s.getCompanyName().length() > 16
                ? s.getCompanyName().substring(0,16) + "…" : s.getCompanyName());
            name.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px; -fx-min-width:140;");
            Region rsp = new Region(); HBox.setHgrow(rsp, Priority.ALWAYS);
            Label price = new Label(String.format("₹%.2f", s.getCurrentPrice()));
            price.setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold; -fx-font-size:12px; -fx-min-width:80; -fx-alignment:CENTER_RIGHT;");
            Label chg = new Label(String.format("%+.2f%%", s.getChangePct()));
            boolean pos = s.getChangePct() >= 0;
            chg.setStyle("-fx-background-color:" + (pos ? "rgba(34,197,94,0.12)" : "rgba(239,68,68,0.12)") +
                "; -fx-background-radius:5; -fx-padding:2 6; -fx-text-fill:" +
                (pos ? "#22c55e" : "#ef4444") + "; -fx-font-size:11px; -fx-font-weight:bold; -fx-min-width:65;");

            row.getChildren().addAll(sym, name, rsp, price, chg);
            watchlistBox.getChildren().add(row);
        }
    }

    // ─── Recent Trades ────────────────────────────────────────────────────────

    private VBox buildRecentTrades() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label title = new Label("🔄 Recent Trades");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setStyle("-fx-text-fill: #e8eaed;");

        VBox rows = new VBox(6);
        List<Transaction> recent = AppController.getInstance().getRecentTransactions(8);

        if (recent.isEmpty()) {
            Label empty = new Label("No trades yet. Head to Market to make your first trade!");
            empty.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px; -fx-padding:16 0;");
            rows.getChildren().add(empty);
        } else {
            for (Transaction t : recent) {
                HBox row = new HBox(16);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 10, 8, 10));
                row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius:8;");

                boolean isBuy = t.isBuy();
                Label typeBadge = new Label(isBuy ? " BUY " : " SELL ");
                typeBadge.setStyle("-fx-background-color:" + (isBuy ? "rgba(34,197,94,0.15)" : "rgba(239,68,68,0.15)") +
                    "; -fx-text-fill:" + (isBuy ? "#22c55e" : "#ef4444") +
                    "; -fx-font-weight:bold; -fx-font-size:10px; -fx-background-radius:4; -fx-padding:3 6;");

                Label sym   = new Label(t.getStockSymbol());
                sym.setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold; -fx-font-size:13px; -fx-min-width:65;");
                Label qty   = new Label(t.getQuantity() + " shares");
                qty.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px; -fx-min-width:80;");
                Region rsp = new Region(); HBox.setHgrow(rsp, Priority.ALWAYS);
                Label amt   = new Label(String.format("₹%.2f", t.getTotalAmount()));
                amt.setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold; -fx-font-size:13px; -fx-min-width:100; -fx-alignment:CENTER_RIGHT;");
                Label ts    = new Label(t.getTimestamp().substring(0,16));
                ts.setStyle("-fx-text-fill:#334155; -fx-font-size:11px; -fx-min-width:130; -fx-alignment:CENTER_RIGHT;");

                row.getChildren().addAll(typeBadge, sym, qty, rsp, amt, ts);
                rows.getChildren().add(row);
            }
        }

        card.getChildren().addAll(title, rows);
        return card;
    }

    // ─── Live Data Refresh ────────────────────────────────────────────────────

    private void refreshLiveData(List<Stock> stocks) {
        AppController app = AppController.getInstance();
        if (app.getCurrentUser() == null) return;
        app.syncPortfolioPrices();

        double cashBalance   = app.getCurrentUser().getWalletBalance();
        double portfolioVal  = app.getPortfolio() != null ? app.getPortfolio().getTotalCurrentValue() : 0;
        double totalVal      = cashBalance + portfolioVal;
        double pl            = app.getPortfolio() != null ? app.getPortfolio().getTotalProfitOrLoss() : 0;
        double plPct         = app.getPortfolio() != null ? app.getPortfolio().getOverallReturnPct() : 0;

        portfolioValueLbl.setText(String.format("₹%.2f", totalVal));
        portfolioPLLbl.setText(String.format("P&L: ₹%+.2f (%.2f%%)", pl, plPct));
        portfolioPLLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" +
            (pl >= 0 ? "#22c55e" : "#ef4444") + ";");
        cashLbl.setText(String.format("₹%.2f", cashBalance));
        refreshWatchlist();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private VBox statCard(String title, Label valueLabel, Label subLabel, String bgColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius:14; " +
            "-fx-border-color: rgba(255,255,255,0.05); -fx-border-width:1; -fx-border-radius:14; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);");
        Label t = new Label(title);
        t.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px; -fx-font-weight:bold;");
        card.getChildren().addAll(t, valueLabel, subLabel);
        return card;
    }

    private Label labelOf(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");
        return l;
    }

    public Parent getRoot() { return root; }
}
