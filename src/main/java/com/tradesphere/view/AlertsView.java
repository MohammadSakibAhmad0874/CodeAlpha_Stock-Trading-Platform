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

import java.util.List;

/**
 * Price alerts CRUD view — create, view, delete alerts.
 */
public class AlertsView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);
    private final ObservableList<PriceAlert> alertData = FXCollections.observableArrayList();

    public AlertsView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");
        buildContent();
        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#0d0f14; -fx-background:#0d0f14;");
    }

    private void buildContent() {
        VBox header = new VBox(4);
        Label title = new Label("🔔 Price Alerts");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill:#e8eaed;");
        Label sub = new Label("Get notified when stocks hit your target prices");
        sub.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");
        header.getChildren().addAll(title, sub);

        VBox createCard = buildCreateAlertCard();
        VBox alertsTable = buildAlertsTable();

        content.getChildren().addAll(header, createCard, alertsTable);
        refreshAlerts();
    }

    // ─── Create Alert Form ────────────────────────────────────────────────────

    private VBox buildCreateAlertCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));

        Label t = new Label("➕ Create New Alert");
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        t.setStyle("-fx-text-fill:#e8eaed;");

        HBox form = new HBox(12);
        form.setAlignment(Pos.CENTER_LEFT);

        // Stock selector
        ComboBox<String> stockBox = new ComboBox<>();
        stockBox.setPromptText("Select Stock");
        stockBox.setPrefWidth(200);
        stockBox.getStyleClass().add("combo-box");
        MarketSimulator.getInstance().getStockList()
            .forEach(s -> stockBox.getItems().add(s.getSymbol() + " – " + s.getCompanyName()));

        // Condition
        ComboBox<String> condBox = new ComboBox<>();
        condBox.getItems().addAll("Price rises above (≥)", "Price drops below (≤)");
        condBox.setValue("Price rises above (≥)");
        condBox.getStyleClass().add("combo-box");
        condBox.setPrefWidth(190);

        // Target price
        TextField priceField = new TextField();
        priceField.setPromptText("Target Price (₹)");
        priceField.getStyleClass().add("text-field");
        priceField.setPrefWidth(140);

        Label resultLbl = new Label();
        resultLbl.setStyle("-fx-font-size:12px;");

        // Current price hint
        Label curPriceLbl = new Label();
        curPriceLbl.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px;");
        stockBox.setOnAction(e -> {
            String sel = stockBox.getValue();
            if (sel != null) {
                String sym = sel.split(" – ")[0];
                Stock s = MarketSimulator.getInstance().getStock(sym);
                if (s != null)
                    curPriceLbl.setText("Current: ₹" + String.format("%.2f", s.getCurrentPrice()));
            }
        });

        Button createBtn = new Button("🔔  Set Alert");
        createBtn.getStyleClass().add("btn-primary");
        createBtn.setOnAction(e -> {
            String sel = stockBox.getValue();
            if (sel == null) { resultLbl.setText("⚠ Select a stock."); return; }
            String sym = sel.split(" – ")[0];
            String company = sel.contains(" – ") ? sel.split(" – ")[1] : sym;
            double price;
            try { price = Double.parseDouble(priceField.getText().trim()); }
            catch (NumberFormatException ex) { resultLbl.setText("⚠ Enter a valid price."); return; }

            PriceAlert.Condition cond = condBox.getValue().contains("above")
                ? PriceAlert.Condition.ABOVE : PriceAlert.Condition.BELOW;
            AlertController.AlertResult res =
                AlertController.getInstance().createAlert(sym, company, cond, price);
            resultLbl.setText(res.success() ? "✅ " + res.message() : "❌ " + res.message());
            resultLbl.setStyle("-fx-font-size:12px; -fx-text-fill:" + (res.success() ? "#22c55e" : "#ef4444") + ";");
            if (res.success()) { priceField.clear(); refreshAlerts(); }
        });

        VBox leftCol = new VBox(4, stockBox, curPriceLbl);
        form.getChildren().addAll(leftCol, condBox, priceField, createBtn, resultLbl);
        card.getChildren().addAll(t, form);
        return card;
    }

    // ─── Alerts Table ─────────────────────────────────────────────────────────

    private VBox buildAlertsTable() {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");

        Label t = new Label("📋 My Alerts");
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        t.setStyle("-fx-text-fill:#e8eaed; -fx-padding:16 20 12 20;");

        TableView<PriceAlert> table = new TableView<>(alertData);
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(360);
        table.setPlaceholder(new Label("No alerts set yet."));

        TableColumn<PriceAlert, String> symCol = new TableColumn<>("SYMBOL");
        symCol.setCellValueFactory(new PropertyValueFactory<>("stockSymbol"));
        symCol.setPrefWidth(90);
        symCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                Label l = new Label(s);
                l.setStyle("-fx-text-fill:#3b82f6; -fx-font-weight:bold;");
                setGraphic(l);
            }
        });

        TableColumn<PriceAlert, String> nameCol = new TableColumn<>("COMPANY");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        nameCol.setPrefWidth(180);

        TableColumn<PriceAlert, Void> condCol = new TableColumn<>("CONDITION");
        condCol.setPrefWidth(200);
        condCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                PriceAlert a = getTableView().getItems().get(getIndex());
                Label l = new Label(a.getConditionLabel() + " ₹" + String.format("%.2f", a.getTargetPrice()));
                l.setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold;");
                setGraphic(l);
            }
        });

        TableColumn<PriceAlert, Void> curPriceCol = new TableColumn<>("CURRENT PRICE");
        curPriceCol.setPrefWidth(130);
        curPriceCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                PriceAlert a = getTableView().getItems().get(getIndex());
                Stock s = MarketSimulator.getInstance().getStock(a.getStockSymbol());
                Label l = new Label(s != null ? String.format("₹%.2f", s.getCurrentPrice()) : "–");
                l.setStyle("-fx-text-fill:#e8eaed;");
                setGraphic(l);
            }
        });

        TableColumn<PriceAlert, Void> statusCol = new TableColumn<>("STATUS");
        statusCol.setPrefWidth(110);
        statusCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                PriceAlert a = getTableView().getItems().get(getIndex());
                String style = switch (a.getStatus()) {
                    case ACTIVE    -> "-fx-background-color:rgba(34,197,94,0.12); -fx-text-fill:#22c55e;";
                    case TRIGGERED -> "-fx-background-color:rgba(245,158,11,0.12); -fx-text-fill:#f59e0b;";
                    case DISMISSED -> "-fx-background-color:rgba(100,116,139,0.12); -fx-text-fill:#64748b;";
                };
                Label l = new Label(a.getStatus().name());
                l.setStyle(style + " -fx-background-radius:5; -fx-padding:2 8; -fx-font-size:11px; -fx-font-weight:bold;");
                setGraphic(l);
            }
        });

        TableColumn<PriceAlert, String> createdCol = new TableColumn<>("CREATED AT");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdCol.setPrefWidth(150);
        createdCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s);
                setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");
            }
        });

        TableColumn<PriceAlert, Void> actCol = new TableColumn<>("DELETE");
        actCol.setPrefWidth(80);
        actCol.setCellFactory(c -> new TableCell<>() {
            private final Button delBtn = new Button("🗑");
            { delBtn.setStyle("-fx-background-color:rgba(239,68,68,0.15); -fx-text-fill:#ef4444; " +
                  "-fx-background-radius:6; -fx-padding:4 8; -fx-cursor:hand;");
              delBtn.setOnAction(e -> {
                  PriceAlert a = getTableView().getItems().get(getIndex());
                  AlertController.getInstance().deleteAlert(a.getId());
                  refreshAlerts();
              });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : delBtn);
            }
        });

        table.getColumns().addAll(symCol, nameCol, condCol, curPriceCol, statusCol, createdCol, actCol);
        // Refresh current prices on tick
        MarketSimulator.getInstance().addListener(s -> table.refresh());

        card.getChildren().addAll(t, table);
        return card;
    }

    private void refreshAlerts() {
        List<PriceAlert> alerts = AppController.getInstance().getAlerts();
        alertData.setAll(alerts);
    }

    public Parent getRoot() { return root; }
}
