package com.tradesphere.view;

import com.tradesphere.controller.AppController;
import com.tradesphere.database.DatabaseManager;
import com.tradesphere.model.*;
import com.tradesphere.services.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

/**
 * Settings page: theme, notifications, currency, animation speed, auto-save.
 */
public class SettingsView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);

    public SettingsView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");
        buildContent();
        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#0d0f14; -fx-background:#0d0f14;");
    }

    private void buildContent() {
        VBox header = new VBox(4);
        Label title = new Label("⚙️ Settings");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill:#e8eaed;");
        Label sub = new Label("Customize your TradeSphere Pro experience");
        sub.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");
        header.getChildren().addAll(title, sub);

        content.getChildren().addAll(header,
            buildThemeCard(),
            buildNotificationsCard(),
            buildCurrencyCard(),
            buildMarketCard(),
            buildProfileCard(),
            buildDangerZone()
        );
    }

    // ─── Theme ────────────────────────────────────────────────────────────────

    private VBox buildThemeCard() {
        VBox card = settingsCard("🎨 Appearance");

        ThemeManager.Theme current = ThemeManager.getInstance().getCurrentTheme();

        ToggleGroup tg = new ToggleGroup();
        ToggleButton darkBtn  = themeBtn("🌙  Dark Mode",  tg, current == ThemeManager.Theme.DARK);
        ToggleButton lightBtn = themeBtn("☀️  Light Mode", tg, current == ThemeManager.Theme.LIGHT);

        darkBtn.setOnAction(e -> {
            ThemeManager.getInstance().setTheme(ThemeManager.Theme.DARK);
            saveTheme("DARK");
        });
        lightBtn.setOnAction(e -> {
            ThemeManager.getInstance().setTheme(ThemeManager.Theme.LIGHT);
            saveTheme("LIGHT");
        });

        HBox btnRow = new HBox(12, darkBtn, lightBtn);
        Label note = new Label("Theme preference is saved automatically.");
        note.setStyle("-fx-text-fill:#334155; -fx-font-size:11px;");

        card.getChildren().addAll(settingRow("Color Theme", btnRow), note);
        return card;
    }

    private void saveTheme(String theme) {
        User u = AppController.getInstance().getCurrentUser();
        if (u == null) return;
        u.setTheme(theme);
        DatabaseManager.getInstance().saveUser(u);
        AppController.getInstance().logActivity(ActivityLog.Action.THEME_CHANGE, "Theme changed to " + theme);
        NotificationManager.getInstance().info("Theme Updated", "Switched to " + theme + " mode.");
    }

    private ToggleButton themeBtn(String text, ToggleGroup tg, boolean selected) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(tg);
        btn.setSelected(selected);
        btn.setStyle("-fx-background-color:" + (selected ? "#3b82f6" : "#1e2436") +
            "; -fx-text-fill:" + (selected ? "white" : "#94a3b8") +
            "; -fx-background-radius:10; -fx-padding:10 24; -fx-cursor:hand; -fx-font-size:13px;");
        btn.selectedProperty().addListener((o,ov,nv) ->
            btn.setStyle("-fx-background-color:" + (nv ? "#3b82f6" : "#1e2436") +
                "; -fx-text-fill:" + (nv ? "white" : "#94a3b8") +
                "; -fx-background-radius:10; -fx-padding:10 24; -fx-cursor:hand; -fx-font-size:13px;"));
        return btn;
    }

    // ─── Notifications ────────────────────────────────────────────────────────

    private VBox buildNotificationsCard() {
        VBox card = settingsCard("🔔 Notifications");
        User u = AppController.getInstance().getCurrentUser();
        boolean enabled = u == null || u.isNotificationsEnabled();

        CheckBox tradeNotif   = settingsCheck("Trade confirmations (buy/sell)",        true);
        CheckBox alertNotif   = settingsCheck("Price alert triggers",                  enabled);
        CheckBox marketNotif  = settingsCheck("Market high/low milestones",            enabled);
        CheckBox systemNotif  = settingsCheck("System & account notifications",        true);

        Button saveBtn = new Button("Save Preferences");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            if (u != null) {
                u.setNotificationsEnabled(alertNotif.isSelected());
                DatabaseManager.getInstance().saveUser(u);
                NotificationManager.getInstance().success("Settings Saved", "Notification preferences updated.");
            }
        });

        card.getChildren().addAll(tradeNotif, alertNotif, marketNotif, systemNotif, saveBtn);
        return card;
    }

    // ─── Currency ─────────────────────────────────────────────────────────────

    private VBox buildCurrencyCard() {
        VBox card = settingsCard("💱 Default Currency");
        User u = AppController.getInstance().getCurrentUser();

        ComboBox<String> currencyBox = new ComboBox<>();
        currencyBox.getItems().addAll("INR – Indian Rupee (₹)", "USD – US Dollar ($)",
            "EUR – Euro (€)", "GBP – British Pound (£)", "JPY – Japanese Yen (¥)");
        String cur = u != null ? u.getCurrency() : "INR";
        currencyBox.setValue(cur.equals("INR") ? "INR – Indian Rupee (₹)" : "USD – US Dollar ($)");
        currencyBox.getStyleClass().add("combo-box");
        currencyBox.setPrefWidth(260);

        Button saveBtn = new Button("Apply Currency");
        saveBtn.getStyleClass().add("btn-outline");
        saveBtn.setOnAction(e -> {
            if (u != null) {
                String selected = currencyBox.getValue().split(" – ")[0];
                u.setCurrency(selected);
                DatabaseManager.getInstance().saveUser(u);
                NotificationManager.getInstance().info("Currency Updated", "Default currency set to " + selected);
            }
        });

        Label note = new Label("Note: Prices are always simulated in INR regardless of display currency.");
        note.setStyle("-fx-text-fill:#334155; -fx-font-size:11px;");

        card.getChildren().addAll(settingRow("Display Currency", new HBox(12, currencyBox, saveBtn)), note);
        return card;
    }

    // ─── Market Settings ──────────────────────────────────────────────────────

    private VBox buildMarketCard() {
        VBox card = settingsCard("📈 Market Simulation");

        Label speedLbl = new Label("Tick Speed: 2 seconds");
        speedLbl.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");

        Slider speedSlider = new Slider(1, 5, 2);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPrefWidth(300);
        speedSlider.valueProperty().addListener((o,ov,nv) -> {
            int secs = nv.intValue();
            speedLbl.setText("Tick Speed: " + secs + " second" + (secs > 1 ? "s" : ""));
        });

        CheckBox autoSave = settingsCheck("Auto-save portfolio on every trade", true);
        CheckBox showAnim = settingsCheck("Enable price change animations",      true);

        card.getChildren().addAll(
            settingRow("Price Update Speed", new VBox(6, speedSlider, speedLbl)),
            autoSave, showAnim
        );
        return card;
    }

    // ─── Profile ──────────────────────────────────────────────────────────────

    private VBox buildProfileCard() {
        VBox card = settingsCard("👤 Profile");
        User u = AppController.getInstance().getCurrentUser();
        if (u == null) return card;

        TextField nameField  = styledField(u.getFullName());
        TextField emailField = styledField(u.getEmail());
        TextField userField  = styledField(u.getUsername());
        userField.setEditable(false);
        userField.setStyle(userField.getStyle() + " -fx-opacity:0.6;");

        Button saveBtn = new Button("💾  Update Profile");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            u.setFullName(nameField.getText().trim());
            u.setEmail(emailField.getText().trim());
            DatabaseManager.getInstance().saveUser(u);
            AppController.getInstance().logActivity(ActivityLog.Action.PROFILE_UPDATE, "Profile updated");
            NotificationManager.getInstance().success("Profile Updated", "Your profile has been saved.");
        });

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(12);
        String[][] rows = {{"Full Name", null}, {"Email", null}, {"Username", null}};
        TextField[] fields = {nameField, emailField, userField};
        for (int i = 0; i < 3; i++) {
            Label l = new Label(rows[i][0]);
            l.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px; -fx-font-weight:bold; -fx-min-width:100;");
            grid.add(l, 0, i);
            fields[i].setPrefWidth(280);
            grid.add(fields[i], 1, i);
        }

        card.getChildren().addAll(grid, saveBtn);
        return card;
    }

    // ─── Danger Zone ──────────────────────────────────────────────────────────

    private VBox buildDangerZone() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:#1a1010; -fx-background-radius:14; " +
            "-fx-border-color:#7a1a1a; -fx-border-width:1; -fx-border-radius:14;");

        Label t = new Label("⚠️  Danger Zone");
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        t.setStyle("-fx-text-fill:#ef4444;");

        Button resetBtn = new Button("🔄  Reset Portfolio (₹1,00,000 cash)");
        resetBtn.setStyle("-fx-background-color:rgba(239,68,68,0.1); -fx-text-fill:#ef4444; " +
            "-fx-background-radius:8; -fx-padding:10 20; -fx-cursor:hand; -fx-font-size:13px; " +
            "-fx-border-color:#ef4444; -fx-border-width:1; -fx-border-radius:8;");
        resetBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "This will clear ALL your holdings and reset your balance to ₹1,00,000. Are you sure?",
                ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Reset Portfolio");
            alert.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    User u = AppController.getInstance().getCurrentUser();
                    if (u != null) {
                        u.setWalletBalance(100_000.0);
                        AppController.getInstance().getPortfolio().getHoldings().clear();
                        DatabaseManager.getInstance().saveUser(u);
                        NotificationManager.getInstance().warning("Portfolio Reset", "Balance reset to ₹1,00,000.");
                    }
                }
            });
        });

        Label note = new Label("This action cannot be undone. All holdings and trade history will be preserved in logs.");
        note.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px;");

        card.getChildren().addAll(t, resetBtn, note);
        return card;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private VBox settingsCard(String title) {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(22));
        Label t = new Label(title);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        t.setStyle("-fx-text-fill:#e8eaed;");
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#252d42;");
        card.getChildren().addAll(t, sep);
        return card;
    }

    private HBox settingRow(String label, javafx.scene.Node control) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px; -fx-min-width:160;");
        row.getChildren().addAll(l, control);
        return row;
    }

    private CheckBox settingsCheck(String text, boolean selected) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(selected);
        cb.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
        return cb;
    }

    private TextField styledField(String value) {
        TextField f = new TextField(value);
        f.getStyleClass().add("text-field");
        return f;
    }

    public Parent getRoot() { return root; }
}
