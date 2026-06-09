package com.tradesphere.view;

import com.tradesphere.model.Stock;
import com.tradesphere.services.*;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.util.List;

/**
 * AI-powered BUY/HOLD/SELL recommendation cards with confidence scores.
 */
public class AIInsightsView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);
    private final FlowPane cardsPane = new FlowPane(16, 16);

    public AIInsightsView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");
        buildContent();
        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#0d0f14; -fx-background:#0d0f14;");
        MarketSimulator.getInstance().addListener(stocks -> refreshCards());
    }

    private void buildContent() {
        // Header
        VBox header = new VBox(4);
        Label title = new Label("🤖 AI Market Insights");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill:#e8eaed;");
        Label sub = new Label("Smart BUY/HOLD/SELL signals based on SMA crossover, momentum & trend analysis — refreshed live.");
        sub.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");
        header.getChildren().addAll(title, sub);

        // Disclaimer
        Label disclaimer = new Label(
            "⚠  These are algorithmic signals for educational purposes only. Not financial advice.");
        disclaimer.setStyle("-fx-background-color:rgba(245,158,11,0.1); -fx-text-fill:#f59e0b; " +
            "-fx-background-radius:8; -fx-padding:10 16; -fx-font-size:12px;");

        // Summary row
        HBox summary = buildSummaryRow();

        // Cards area
        cardsPane.setAlignment(Pos.TOP_LEFT);
        refreshCards();

        content.getChildren().addAll(header, disclaimer, summary, cardsPane);
    }

    private HBox buildSummaryRow() {
        List<AIInsightsService.Recommendation> recs =
            AIInsightsService.getInstance().generateAll(MarketSimulator.getInstance().getStockList());

        long buys  = recs.stream().filter(r -> r.signal() == AIInsightsService.Signal.BUY).count();
        long holds = recs.stream().filter(r -> r.signal() == AIInsightsService.Signal.HOLD).count();
        long sells = recs.stream().filter(r -> r.signal() == AIInsightsService.Signal.SELL).count();

        HBox row = new HBox(12);

        VBox b = summaryChip("BUY",  String.valueOf(buys),  "#22c55e", "rgba(34,197,94,0.1)");
        VBox h = summaryChip("HOLD", String.valueOf(holds), "#f59e0b", "rgba(245,158,11,0.1)");
        VBox s = summaryChip("SELL", String.valueOf(sells), "#ef4444", "rgba(239,68,68,0.1)");
        VBox t = summaryChip("TOTAL",String.valueOf(recs.size()), "#3b82f6", "rgba(59,130,246,0.1)");

        row.getChildren().addAll(b, h, s, t);
        return row;
    }

    private VBox summaryChip(String label, String val, String textColor, String bg) {
        VBox chip = new VBox(4);
        chip.setPadding(new Insets(14, 24, 14, 24));
        chip.setAlignment(Pos.CENTER);
        chip.setStyle("-fx-background-color:" + bg + "; -fx-background-radius:12; " +
            "-fx-border-color:" + textColor + "40; -fx-border-width:1; -fx-border-radius:12;");
        Label v = new Label(val);
        v.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        v.setStyle("-fx-text-fill:" + textColor + ";");
        Label l = new Label(label);
        l.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px; -fx-font-weight:bold;");
        chip.getChildren().addAll(v, l);
        return chip;
    }

    private void refreshCards() {
        List<AIInsightsService.Recommendation> recs =
            AIInsightsService.getInstance().generateAll(MarketSimulator.getInstance().getStockList());
        cardsPane.getChildren().clear();
        for (int i = 0; i < recs.size(); i++) {
            AIInsightsService.Recommendation rec = recs.get(i);
            VBox card = buildRecommendationCard(rec);
            cardsPane.getChildren().add(card);
            // Staggered animation
            FadeTransition ft = new FadeTransition(Duration.millis(400), card);
            ft.setDelay(Duration.millis(i * 40L));
            ft.setFromValue(0); ft.setToValue(1);
            ft.play();
        }
    }

    private VBox buildRecommendationCard(AIInsightsService.Recommendation rec) {
        String styleClass = switch (rec.signal()) {
            case BUY  -> "signal-buy";
            case SELL -> "signal-sell";
            case HOLD -> "signal-hold";
        };
        String signalColor = switch (rec.signal()) {
            case BUY  -> "#22c55e";
            case SELL -> "#ef4444";
            case HOLD -> "#f59e0b";
        };
        String signalIcon = switch (rec.signal()) {
            case BUY  -> "▲";
            case SELL -> "▼";
            case HOLD -> "◆";
        };

        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setPrefWidth(280);
        card.getStyleClass().add(styleClass);

        // Top row: symbol + signal badge
        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);
        Label sym = new Label(rec.symbol());
        sym.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        sym.setStyle("-fx-text-fill:#e8eaed;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label signalBadge = new Label(signalIcon + " " + rec.signal().name());
        signalBadge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        signalBadge.setStyle("-fx-text-fill:" + signalColor + ";");
        top.getChildren().addAll(sym, sp, signalBadge);

        // Company
        Label company = new Label(rec.companyName());
        company.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px;");

        // Sector badge
        Label sector = new Label(rec.sector());
        sector.setStyle("-fx-background-color:rgba(139,92,246,0.12); -fx-text-fill:#8b5cf6; " +
            "-fx-background-radius:4; -fx-padding:2 8; -fx-font-size:10px;");

        // Confidence bar
        VBox confBox = new VBox(4);
        Label confLabel = new Label("Confidence: " + rec.confidence() + "%");
        confLabel.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");
        ProgressBar confBar = new ProgressBar(rec.confidence() / 100.0);
        confBar.setMaxWidth(Double.MAX_VALUE);
        confBar.setStyle("-fx-accent:" + signalColor + "; -fx-background-color:rgba(255,255,255,0.05); " +
            "-fx-background-radius:4; -fx-pref-height:6;");
        confBox.getChildren().addAll(confLabel, confBar);

        // Current price
        Stock s = MarketSimulator.getInstance().getStock(rec.symbol());
        double price = s != null ? s.getCurrentPrice() : 0;
        double chgPct = s != null ? s.getChangePct() : 0;
        HBox priceRow = new HBox(8);
        priceRow.setAlignment(Pos.CENTER_LEFT);
        Label priceLbl = new Label(String.format("₹%.2f", price));
        priceLbl.setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold; -fx-font-size:15px;");
        Label chgLbl = new Label(String.format("%+.2f%%", chgPct));
        chgLbl.setStyle("-fx-text-fill:" + (chgPct >= 0 ? "#22c55e" : "#ef4444") + "; -fx-font-size:12px; -fx-font-weight:bold;");
        priceRow.getChildren().addAll(priceLbl, chgLbl);

        // Rationale
        Label rationale = new Label(rec.rationale());
        rationale.setWrapText(true);
        rationale.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");
        rationale.setMaxWidth(244);

        card.getChildren().addAll(top, company, sector, priceRow, confBox, rationale);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + " -fx-scale-x:1.02; -fx-scale-y:1.02;"));
        card.setOnMouseExited(e  -> card.setStyle(card.getStyle().replace(" -fx-scale-x:1.02; -fx-scale-y:1.02;","")));

        return card;
    }

    public Parent getRoot() { return root; }
}
