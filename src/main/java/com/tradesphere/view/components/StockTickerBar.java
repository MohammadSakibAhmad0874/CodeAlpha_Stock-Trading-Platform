package com.tradesphere.view.components;

import com.tradesphere.model.Stock;
import com.tradesphere.services.MarketSimulator;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.util.List;

/**
 * Horizontally scrolling stock ticker bar shown at top of MainWindow.
 * Displays symbol, price, and change% for all stocks with infinite loop animation.
 */
public class StockTickerBar {

    private final HBox root = new HBox();
    private final HBox tickerContent = new HBox(32);
    private TranslateTransition scroll;

    public StockTickerBar() {
        build();
        MarketSimulator.getInstance().addListener(stocks -> refreshTicker(stocks));
    }

    private void build() {
        root.getStyleClass().add("ticker-bar");
        root.setPrefHeight(36);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: #111318; -fx-border-color: transparent transparent #252d42 transparent; -fx-border-width: 0 0 1 0;");

        // Left label
        Label marketLabel = new Label("  LIVE MARKET  ");
        marketLabel.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 4 10;");

        // Scrolling container
        StackPane clipBox = new StackPane();
        clipBox.setPrefHeight(36);
        HBox.setHgrow(clipBox, Priority.ALWAYS);

        tickerContent.setAlignment(Pos.CENTER_LEFT);
        tickerContent.setPadding(new Insets(0, 0, 0, 20));

        // Initial population
        refreshTicker(MarketSimulator.getInstance().getStockList());

        clipBox.getChildren().add(tickerContent);
        root.getChildren().addAll(marketLabel, clipBox);
    }

    private void refreshTicker(List<Stock> stocks) {
        if (scroll != null) scroll.stop();
        tickerContent.getChildren().clear();

        // Build two copies for seamless loop
        for (int pass = 0; pass < 2; pass++) {
            for (Stock s : stocks) {
                HBox item = buildTickerItem(s);
                tickerContent.getChildren().add(item);
            }
            // Separator between passes
            Label sep = new Label(" ● ");
            sep.setStyle("-fx-text-fill: #252d42;");
            tickerContent.getChildren().add(sep);
        }

        // Calculate scroll distance
        double totalWidth = stocks.size() * 180.0;

        scroll = new TranslateTransition(Duration.millis(totalWidth * 30), tickerContent);
        scroll.setFromX(0);
        scroll.setToX(-totalWidth);
        scroll.setCycleCount(Animation.INDEFINITE);
        scroll.setInterpolator(Interpolator.LINEAR);
        scroll.play();
    }

    private HBox buildTickerItem(Stock stock) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 8, 0, 8));

        Label sym = new Label(stock.getSymbol());
        sym.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        sym.setStyle("-fx-text-fill: #e8eaed;");

        Label price = new Label();
        stock.currentPriceProperty().addListener((o, ov, nv) -> {
            price.setText(String.format("₹%.2f", nv.doubleValue()));
            boolean up = nv.doubleValue() >= ov.doubleValue();
            price.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" +
                (up ? "#22c55e" : "#ef4444") + ";");
        });
        price.setText(String.format("₹%.2f", stock.getCurrentPrice()));
        price.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:#e8eaed;");

        Label chg = new Label();
        stock.changePctProperty().addListener((o, ov, nv) -> {
            double d = nv.doubleValue();
            chg.setText(String.format("%s%.2f%%", d >= 0 ? "▲" : "▼", Math.abs(d)));
            chg.setStyle("-fx-font-size:10px; -fx-text-fill:" + (d >= 0 ? "#22c55e" : "#ef4444") + ";");
        });
        double cp = stock.getChangePct();
        chg.setText(String.format("%s%.2f%%", cp >= 0 ? "▲" : "▼", Math.abs(cp)));
        chg.setStyle("-fx-font-size:10px; -fx-text-fill:" + (cp >= 0 ? "#22c55e" : "#ef4444") + ";");

        Label divider = new Label("  |  ");
        divider.setStyle("-fx-text-fill:#252d42; -fx-font-size:10px;");

        item.getChildren().addAll(sym, price, chg, divider);
        return item;
    }

    public HBox getRoot() { return root; }
}
