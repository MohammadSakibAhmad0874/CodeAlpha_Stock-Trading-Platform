package com.tradesphere.view;

import com.tradesphere.controller.*;
import com.tradesphere.model.Stock;
import com.tradesphere.services.MarketSimulator;
import javafx.animation.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;

/**
 * Live stock market table with search, filter, buy/sell panel.
 */
public class MarketView {

    private final BorderPane root = new BorderPane();
    private final ObservableList<Stock> stockData = FXCollections.observableArrayList();
    private TableView<Stock> table;
    private TextField searchField;
    private String currentFilter = "ALL";

    public MarketView() { build(); }

    private void build() {
        root.setStyle("-fx-background-color: #0d0f14;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");

        content.getChildren().addAll(buildHeader(), buildFilters(), buildTable(), buildTradePanel());

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0d0f14; -fx-background: #0d0f14;");
        root.setCenter(scroll);

        // Live updates
        stockData.addAll(MarketSimulator.getInstance().getStockList());
        MarketSimulator.getInstance().addListener(stocks -> {
            // Table auto-refreshes via JavaFX properties binding
            table.refresh();
        });
    }

    // ─── Header ───────────────────────────────────────────────────────────────

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("📈 Market Watch");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #e8eaed;");
        Label sub = new Label("Live market prices — updated every 2 seconds");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size:12px;");
        titleBox.getChildren().addAll(title, sub);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        // Search
        searchField = new TextField();
        searchField.setPromptText("🔍  Search symbol or company…");
        searchField.setPrefWidth(260);
        searchField.getStyleClass().add("text-field");
        searchField.textProperty().addListener((o,ov,nv) -> applyFilter());

        // Live indicator
        HBox liveIndicator = new HBox(6);
        liveIndicator.setAlignment(Pos.CENTER);
        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: #22c55e; -fx-font-size:10px;");
        FadeTransition blink = new FadeTransition(Duration.millis(900), dot);
        blink.setFromValue(1); blink.setToValue(0.2);
        blink.setCycleCount(Animation.INDEFINITE); blink.setAutoReverse(true);
        blink.play();
        Label liveLbl = new Label("LIVE");
        liveLbl.setStyle("-fx-text-fill:#22c55e; -fx-font-size:11px; -fx-font-weight:bold;");
        liveIndicator.getChildren().addAll(dot, liveLbl);

        header.getChildren().addAll(titleBox, sp, liveIndicator, searchField);
        return header;
    }

    // ─── Filter Bar ───────────────────────────────────────────────────────────

    private HBox buildFilters() {
        HBox bar = new HBox(8);
        bar.setAlignment(Pos.CENTER_LEFT);

        String[] filters = {"ALL", "TOP GAINERS", "TOP LOSERS", "HIGH VOLUME", "TECHNOLOGY", "FINTECH", "AUTOMOTIVE"};
        ToggleGroup tg = new ToggleGroup();

        for (String f : filters) {
            ToggleButton btn = new ToggleButton(f);
            btn.setToggleGroup(tg);
            btn.setStyle("-fx-background-color: #1e2436; -fx-text-fill:#94a3b8; -fx-background-radius:20; " +
                "-fx-padding:6 14; -fx-cursor:hand; -fx-font-size:11px; -fx-font-weight:bold;");
            btn.selectedProperty().addListener((o,ov,nv) -> {
                if (nv) {
                    btn.setStyle("-fx-background-color:#3b82f6; -fx-text-fill:white; -fx-background-radius:20; " +
                        "-fx-padding:6 14; -fx-cursor:hand; -fx-font-size:11px; -fx-font-weight:bold;");
                    currentFilter = f;
                    applyFilter();
                } else {
                    btn.setStyle("-fx-background-color: #1e2436; -fx-text-fill:#94a3b8; -fx-background-radius:20; " +
                        "-fx-padding:6 14; -fx-cursor:hand; -fx-font-size:11px; -fx-font-weight:bold;");
                }
            });
            if (f.equals("ALL")) { btn.setSelected(true); }
            bar.getChildren().add(btn);
        }
        return bar;
    }

    // ─── Table ───────────────────────────────────────────────────────────────

    private VBox buildTable() {
        table = new TableView<>(stockData);
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(420);
        table.setPlaceholder(new Label("No stocks match your search."));

        // Symbol
        TableColumn<Stock, String> symCol = new TableColumn<>("SYMBOL");
        symCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        symCol.setPrefWidth(80);
        symCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                Label lbl = new Label(s);
                lbl.setStyle("-fx-text-fill:#3b82f6; -fx-font-weight:bold; -fx-font-size:13px;");
                setGraphic(lbl);
            }
        });

        // Company
        TableColumn<Stock, String> nameCol = new TableColumn<>("COMPANY");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        nameCol.setPrefWidth(180);

        // Price
        TableColumn<Stock, Double> priceCol = new TableColumn<>("PRICE");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        priceCol.setPrefWidth(100);
        priceCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setGraphic(null); return; }
                Label lbl = new Label(String.format("₹%.2f", v));
                lbl.setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold; -fx-font-size:13px;");
                setGraphic(lbl);
            }
        });

        // Change %
        TableColumn<Stock, Double> chgCol = new TableColumn<>("CHANGE%");
        chgCol.setCellValueFactory(new PropertyValueFactory<>("changePct"));
        chgCol.setPrefWidth(100);
        chgCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setGraphic(null); return; }
                boolean pos = v >= 0;
                Label lbl = new Label(String.format("%s%.2f%%", pos ? "▲ +" : "▼ ", v));
                lbl.setStyle("-fx-background-color:" + (pos ? "rgba(34,197,94,0.12)" : "rgba(239,68,68,0.12)") +
                    "; -fx-background-radius:5; -fx-padding:3 8; -fx-text-fill:" +
                    (pos ? "#22c55e" : "#ef4444") + "; -fx-font-size:12px; -fx-font-weight:bold;");
                setGraphic(lbl);
            }
        });

        // Day High
        TableColumn<Stock, Double> highCol = new TableColumn<>("HIGH");
        highCol.setCellValueFactory(new PropertyValueFactory<>("dayHigh"));
        highCol.setPrefWidth(90);
        highCol.setCellFactory(col -> numCell("#22c55e"));

        // Day Low
        TableColumn<Stock, Double> lowCol = new TableColumn<>("LOW");
        lowCol.setCellValueFactory(new PropertyValueFactory<>("dayLow"));
        lowCol.setPrefWidth(90);
        lowCol.setCellFactory(col -> numCell("#ef4444"));

        // Volume
        TableColumn<Stock, Long> volCol = new TableColumn<>("VOLUME");
        volCol.setCellValueFactory(new PropertyValueFactory<>("volume"));
        volCol.setPrefWidth(110);
        volCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Long v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(formatVolume(v));
                setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");
            }
        });

        // Sector
        TableColumn<Stock, String> sectorCol = new TableColumn<>("SECTOR");
        sectorCol.setCellValueFactory(new PropertyValueFactory<>("sector"));
        sectorCol.setPrefWidth(110);
        sectorCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                Label lbl = new Label(s);
                lbl.setStyle("-fx-background-color:rgba(139,92,246,0.12); -fx-text-fill:#8b5cf6; " +
                    "-fx-background-radius:5; -fx-padding:2 8; -fx-font-size:11px;");
                setGraphic(lbl);
            }
        });

        // Action
        TableColumn<Stock, Void> actionCol = new TableColumn<>("ACTION");
        actionCol.setPrefWidth(140);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button buyBtn  = new Button("Buy");
            private final Button sellBtn = new Button("Sell");
            private final HBox box = new HBox(6, buyBtn, sellBtn);
            { box.setAlignment(Pos.CENTER);
              buyBtn.setStyle("-fx-background-color:#16a34a; -fx-text-fill:white; -fx-background-radius:6; " +
                  "-fx-padding:4 12; -fx-cursor:hand; -fx-font-size:11px; -fx-font-weight:bold;");
              sellBtn.setStyle("-fx-background-color:#dc2626; -fx-text-fill:white; -fx-background-radius:6; " +
                  "-fx-padding:4 12; -fx-cursor:hand; -fx-font-size:11px; -fx-font-weight:bold;");
              buyBtn.setOnAction(e -> {
                  Stock s = getTableView().getItems().get(getIndex());
                  showTradeDialog(s, true);
              });
              sellBtn.setOnAction(e -> {
                  Stock s = getTableView().getItems().get(getIndex());
                  showTradeDialog(s, false);
              });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(symCol, nameCol, priceCol, chgCol, highCol, lowCol, volCol, sectorCol, actionCol);

        // Refresh price columns on tick
        MarketSimulator.getInstance().addListener(stocks -> table.refresh());

        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        card.getChildren().add(table);
        return card;
    }

    // ─── Inline Trade Panel ────────────────────────────────────────────────────

    private VBox buildTradePanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(20));
        panel.getStyleClass().add("card");

        Label title = new Label("⚡ Quick Trade");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setStyle("-fx-text-fill:#e8eaed;");

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> symBox = new ComboBox<>();
        symBox.setPromptText("Select Stock");
        symBox.setPrefWidth(180);
        symBox.getStyleClass().add("combo-box");
        MarketSimulator.getInstance().getStockList()
            .forEach(s -> symBox.getItems().add(s.getSymbol() + " – " + s.getCompanyName()));

        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");
        qtyField.setPrefWidth(100);
        qtyField.getStyleClass().add("text-field");

        Label priceInfo = new Label("Price: –");
        priceInfo.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px; -fx-min-width:130;");

        symBox.setOnAction(e -> {
            String sel = symBox.getValue();
            if (sel != null) {
                String sym = sel.split(" – ")[0];
                Stock s = MarketSimulator.getInstance().getStock(sym);
                if (s != null) priceInfo.setText(String.format("₹%.2f / share", s.getCurrentPrice()));
            }
        });

        Label resultLbl = new Label();
        resultLbl.setWrapText(true);
        resultLbl.setStyle("-fx-font-size:12px;");

        Button buyBtn = new Button("✅ Buy");
        buyBtn.getStyleClass().add("btn-success");
        buyBtn.setOnAction(e -> executeTrade(symBox, qtyField, resultLbl, true));

        Button sellBtn = new Button("❌ Sell");
        sellBtn.getStyleClass().add("btn-danger");
        sellBtn.setOnAction(e -> executeTrade(symBox, qtyField, resultLbl, false));

        row.getChildren().addAll(symBox, qtyField, priceInfo, buyBtn, sellBtn, resultLbl);
        panel.getChildren().addAll(title, row);
        return panel;
    }

    private void executeTrade(ComboBox<String> symBox, TextField qtyField, Label resultLbl, boolean buy) {
        String sel = symBox.getValue();
        if (sel == null) { resultLbl.setText("⚠ Select a stock."); return; }
        String sym = sel.split(" – ")[0];
        int qty;
        try { qty = Integer.parseInt(qtyField.getText().trim()); }
        catch (NumberFormatException ex) { resultLbl.setText("⚠ Enter a valid quantity."); return; }
        TradeController.TradeResult res = buy
            ? TradeController.getInstance().buy(sym, qty)
            : TradeController.getInstance().sell(sym, qty);
        resultLbl.setText(res.message());
        resultLbl.setStyle("-fx-font-size:12px; -fx-text-fill:" + (res.success() ? "#22c55e" : "#ef4444") + ";");
        if (res.success()) qtyField.clear();
    }

    private void showTradeDialog(Stock stock, boolean buy) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle((buy ? "Buy" : "Sell") + " – " + stock.getSymbol());
        dlg.setHeaderText((buy ? "🟢 Buy " : "🔴 Sell ") + stock.getCompanyName());
        dlg.getDialogPane().getButtonTypes().addAll(
            new ButtonType(buy ? "Confirm Buy" : "Confirm Sell", ButtonBar.ButtonData.OK_DONE),
            ButtonType.CANCEL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        Label priceLbl = new Label("Current Price: " + String.format("₹%.2f", stock.getCurrentPrice()));
        priceLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");
        TextField qtyField = new TextField("1");
        qtyField.setPromptText("Quantity");
        Label totalLbl = new Label("Total: ₹" + String.format("%.2f", stock.getCurrentPrice()));
        qtyField.textProperty().addListener((o,ov,nv) -> {
            try { int q = Integer.parseInt(nv);
                totalLbl.setText("Total: ₹" + String.format("%.2f", q * stock.getCurrentPrice()));
            } catch (Exception ignored) {}
        });
        content.getChildren().addAll(priceLbl, new Label("Quantity:"), qtyField, totalLbl);
        dlg.getDialogPane().setContent(content);

        dlg.showAndWait().ifPresent(bt -> {
            if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    TradeController.TradeResult res = buy
                        ? TradeController.getInstance().buy(stock.getSymbol(), qty)
                        : TradeController.getInstance().sell(stock.getSymbol(), qty);
                    if (!res.success()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, res.message());
                        alert.setTitle("Trade Failed"); alert.showAndWait();
                    }
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    private void applyFilter() {
        String search = searchField.getText().toLowerCase().trim();
        stockData.setAll(MarketSimulator.getInstance().getStockList().stream()
            .filter(s -> {
                boolean matchSearch = search.isEmpty()
                    || s.getSymbol().toLowerCase().contains(search)
                    || s.getCompanyName().toLowerCase().contains(search);
                boolean matchFilter = switch (currentFilter) {
                    case "TOP GAINERS" -> s.getChangePct() >= 0;
                    case "TOP LOSERS"  -> s.getChangePct() < 0;
                    case "HIGH VOLUME" -> s.getVolume() > 500_000_000L;
                    case "TECHNOLOGY"  -> s.getSector().equalsIgnoreCase("Technology");
                    case "FINTECH"     -> s.getSector().equalsIgnoreCase("Fintech");
                    case "AUTOMOTIVE"  -> s.getSector().equalsIgnoreCase("Automotive");
                    default            -> true;
                };
                return matchSearch && matchFilter;
            }).toList());
    }

    private <T> TableCell<Stock, T> numCell(String color) {
        return new TableCell<>() {
            @Override protected void updateItem(T v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(String.format("₹%.2f", ((Number)v).doubleValue()));
                setStyle("-fx-text-fill:" + color + "; -fx-font-size:12px;");
            }
        };
    }

    private String formatVolume(long v) {
        if (v >= 1_000_000_000) return String.format("%.1fB", v/1_000_000_000.0);
        if (v >= 1_000_000)     return String.format("%.1fM", v/1_000_000.0);
        return String.format("%,d", v);
    }

    public Parent getRoot() { return root; }
}
