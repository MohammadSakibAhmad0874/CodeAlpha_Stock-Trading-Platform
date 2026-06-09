package com.tradesphere.view;

import com.tradesphere.controller.*;
import com.tradesphere.model.*;
import com.tradesphere.services.MarketSimulator;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.*;

/**
 * Portfolio holdings table with analytics cards.
 */
public class PortfolioView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);
    private final ObservableList<Portfolio.Holding> holdingData = FXCollections.observableArrayList();
    private Label totalInvestedLbl, totalValueLbl, totalPLLbl, roiLbl;

    public PortfolioView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");

        buildContent();

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #0d0f14; -fx-background: #0d0f14;");

        MarketSimulator.getInstance().addListener(stocks -> {
            AppController.getInstance().syncPortfolioPrices();
            refreshHoldings();
            updateAnalytics();
        });
    }

    private void buildContent() {
        // Header
        Label title = new Label("💼 My Portfolio");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #e8eaed;");
        Label sub = new Label("Track your holdings and performance in real time");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size:12px;");
        VBox header = new VBox(4, title, sub);

        // Analytics row
        HBox analytics = buildAnalyticsRow();

        // Holdings table
        VBox table = buildHoldingsTable();

        content.getChildren().addAll(header, analytics, table);
        updateAnalytics();
        refreshHoldings();
    }

    // ─── Analytics Cards ──────────────────────────────────────────────────────

    private HBox buildAnalyticsRow() {
        totalInvestedLbl = bigLabel("₹0.00");
        totalValueLbl    = bigLabel("₹0.00");
        totalPLLbl       = bigLabel("₹0.00");
        roiLbl           = bigLabel("0.00%");

        VBox c1 = analyticsCard("💰 Total Invested",    totalInvestedLbl, "#1e2436");
        VBox c2 = analyticsCard("📈 Current Value",     totalValueLbl,    "#1a3320");
        VBox c3 = analyticsCard("📊 Net Profit/Loss",   totalPLLbl,       "#1e1a3f");
        VBox c4 = analyticsCard("🎯 Overall ROI",       roiLbl,           "#1f1a14");

        HBox row = new HBox(16, c1, c2, c3, c4);
        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);
        return row;
    }

    private VBox analyticsCard(String label, Label valueLabel, String bg) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle("-fx-background-color:" + bg + "; -fx-background-radius:14; " +
            "-fx-border-color: rgba(255,255,255,0.05); -fx-border-width:1; -fx-border-radius:14;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px; -fx-font-weight:bold;");
        card.getChildren().addAll(lbl, valueLabel);
        return card;
    }

    private Label bigLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        l.setStyle("-fx-text-fill:#e8eaed;");
        return l;
    }

    // ─── Holdings Table ───────────────────────────────────────────────────────

    private VBox buildHoldingsTable() {
        TableView<Portfolio.Holding> table = new TableView<>(holdingData);
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        table.setPlaceholder(new Label("No holdings yet. Buy stocks from Market Watch!"));

        // Symbol
        TableColumn<Portfolio.Holding, String> symCol = col("SYMBOL", "symbol", 80);
        symCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                Label lbl = new Label(s);
                lbl.setStyle("-fx-text-fill:#3b82f6; -fx-font-weight:bold;");
                setGraphic(lbl);
            }
        });

        TableColumn<Portfolio.Holding, String> nameCol  = col("COMPANY",    "companyName", 180);
        TableColumn<Portfolio.Holding, Integer> qtyCol  = col("QTY",        "quantity",    70);
        qtyCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(String.valueOf(v));
                setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold; -fx-alignment:CENTER;");
            }
        });

        TableColumn<Portfolio.Holding, Double> avgPriceCol = col("AVG BUY PRICE", "avgBuyPrice", 130);
        avgPriceCol.setCellFactory(c -> priceCellFormat("#94a3b8"));

        TableColumn<Portfolio.Holding, Double> curPriceCol = col("CUR PRICE", "currentPrice", 110);
        curPriceCol.setCellFactory(c -> priceCellFormat("#e8eaed"));

        TableColumn<Portfolio.Holding, Void> plCol = new TableColumn<>("P&L");
        plCol.setPrefWidth(130);
        plCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Portfolio.Holding h = getTableView().getItems().get(getIndex());
                double pl = h.getProfitOrLoss();
                Label lbl = new Label(String.format("₹%+.2f", pl));
                lbl.setStyle("-fx-text-fill:" + (pl >= 0 ? "#22c55e" : "#ef4444") +
                    "; -fx-font-weight:bold; -fx-font-size:13px;");
                setGraphic(lbl);
            }
        });

        TableColumn<Portfolio.Holding, Void> retCol = new TableColumn<>("RETURN %");
        retCol.setPrefWidth(110);
        retCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Portfolio.Holding h = getTableView().getItems().get(getIndex());
                double pct = h.getReturnPct();
                Label lbl = new Label(String.format("%+.2f%%", pct));
                lbl.setStyle("-fx-background-color:" + (pct >= 0 ? "rgba(34,197,94,0.12)" : "rgba(239,68,68,0.12)") +
                    "; -fx-background-radius:5; -fx-padding:3 8; -fx-text-fill:" +
                    (pct >= 0 ? "#22c55e" : "#ef4444") + "; -fx-font-weight:bold; -fx-font-size:12px;");
                setGraphic(lbl);
            }
        });

        TableColumn<Portfolio.Holding, Void> actCol = new TableColumn<>("SELL");
        actCol.setPrefWidth(80);
        actCol.setCellFactory(c -> new TableCell<>() {
            private final Button sellBtn = new Button("Sell");
            { sellBtn.setStyle("-fx-background-color:#dc2626; -fx-text-fill:white; -fx-background-radius:6; " +
                  "-fx-padding:4 12; -fx-cursor:hand; -fx-font-size:11px; -fx-font-weight:bold;");
              sellBtn.setOnAction(e -> {
                  Portfolio.Holding h = getTableView().getItems().get(getIndex());
                  showSellDialog(h);
              });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : sellBtn);
            }
        });

        table.getColumns().addAll(symCol, nameCol, qtyCol, avgPriceCol, curPriceCol, plCol, retCol, actCol);

        // Auto-refresh on market tick
        MarketSimulator.getInstance().addListener(s -> table.refresh());

        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        Label hdr = new Label("📋 Holdings");
        hdr.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        hdr.setStyle("-fx-text-fill:#e8eaed; -fx-padding:16 20 12 20;");
        card.getChildren().addAll(hdr, table);
        return card;
    }

    private void showSellDialog(Portfolio.Holding holding) {
        TextInputDialog dlg = new TextInputDialog("1");
        dlg.setTitle("Sell " + holding.getSymbol());
        dlg.setHeaderText("Sell shares of " + holding.getCompanyName());
        dlg.setContentText(String.format("You own %d shares. Quantity to sell:", holding.getQuantity()));
        dlg.showAndWait().ifPresent(input -> {
            try {
                int qty = Integer.parseInt(input.trim());
                TradeController.TradeResult res = TradeController.getInstance().sell(holding.getSymbol(), qty);
                if (!res.success()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, res.message());
                    alert.setTitle("Sell Failed"); alert.showAndWait();
                }
                refreshHoldings();
            } catch (NumberFormatException ignored) {}
        });
    }

    private void refreshHoldings() {
        Portfolio p = AppController.getInstance().getPortfolio();
        if (p == null) return;
        List<Portfolio.Holding> list = new ArrayList<>(p.getHoldings().values());
        holdingData.setAll(list);
    }

    private void updateAnalytics() {
        Portfolio p = AppController.getInstance().getPortfolio();
        if (p == null) return;
        double invested = p.getTotalInvested();
        double value    = p.getTotalCurrentValue();
        double pl       = p.getTotalProfitOrLoss();
        double roi      = p.getOverallReturnPct();

        totalInvestedLbl.setText(String.format("₹%.2f", invested));
        totalValueLbl.setText(String.format("₹%.2f", value));
        totalPLLbl.setText(String.format("₹%+.2f", pl));
        roiLbl.setText(String.format("%+.2f%%", roi));
        totalPLLbl.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:" + (pl >= 0 ? "#22c55e" : "#ef4444") + ";");
        roiLbl.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:" + (roi >= 0 ? "#22c55e" : "#ef4444") + ";");
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Portfolio.Holding, T> col(String header, String prop, double width) {
        TableColumn<Portfolio.Holding, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private <T extends Number> TableCell<Portfolio.Holding, T> priceCellFormat(String color) {
        return new TableCell<>() {
            @Override protected void updateItem(T v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(String.format("₹%.2f", v.doubleValue()));
                setStyle("-fx-text-fill:" + color + "; -fx-font-size:13px;");
            }
        };
    }

    public Parent getRoot() { return root; }
}
