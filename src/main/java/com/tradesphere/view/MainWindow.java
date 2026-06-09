package com.tradesphere.view;

import com.tradesphere.App;
import com.tradesphere.controller.*;
import com.tradesphere.services.*;
import com.tradesphere.view.components.StockTickerBar;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main application shell: sidebar navigation + content area + ticker bar.
 * All views are loaded into the content pane on demand.
 */
public class MainWindow {

    private final BorderPane root = new BorderPane();
    private final StackPane  contentPane = new StackPane();
    private Button activeNavBtn;

    // View instances (lazy loaded)
    private DashboardView  dashboardView;
    private MarketView     marketView;
    private PortfolioView  portfolioView;
    private ChartsView     chartsView;
    private AIInsightsView aiView;
    private LeaderboardView leaderView;
    private ReportsView    reportsView;
    private AlertsView     alertsView;
    private SettingsView   settingsView;
    private AboutView      aboutView;

    public MainWindow() { build(); }

    private void build() {
        root.setStyle("-fx-background-color: #0d0f14;");

        root.setLeft(buildSidebar());
        root.setTop(buildTickerBar());
        root.setCenter(contentPane);

        // Start on dashboard
        navigateTo("DASHBOARD");

        // Subscribe market simulator to check alerts on every tick
        MarketSimulator.getInstance().addListener(stocks ->
            AlertController.getInstance().checkAlerts(stocks));
    }

    // ─── Sidebar ──────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(0, 12, 16, 12));
        sidebar.setPrefWidth(220);

        // Logo
        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(20, 8, 20, 8));
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logo = new Label("🌐 TradeSphere");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        logo.setStyle("-fx-text-fill: #3b82f6;");
        Label sub = new Label("Pro  •  Learn, Trade, Analyze.");
        sub.setStyle("-fx-font-size: 9px; -fx-text-fill: #334155;");
        logoBox.getChildren().addAll(logo, sub);

        // User info chip
        AppController app = AppController.getInstance();
        String name = app.getCurrentUser() != null ? app.getCurrentUser().getFullName() : "Guest";
        HBox userChip = new HBox(10);
        userChip.setAlignment(Pos.CENTER_LEFT);
        userChip.setPadding(new Insets(10, 8, 16, 8));
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size:20px;");
        VBox userInfo = new VBox(1);
        Label uName = new Label(name);
        uName.setStyle("-fx-text-fill:#e8eaed; -fx-font-size:13px; -fx-font-weight:bold;");
        Label uBal  = new Label("₹" + String.format("%.0f",
            app.getCurrentUser() != null ? app.getCurrentUser().getWalletBalance() : 0));
        uBal.setStyle("-fx-text-fill:#22c55e; -fx-font-size:11px;");
        userInfo.getChildren().addAll(uName, uBal);
        userChip.getChildren().addAll(avatar, userInfo);

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #1e2436;");

        // Navigation items
        Label mainLbl = navSectionLabel("MAIN");
        Button dashBtn    = navBtn("📊", "Dashboard",    "DASHBOARD");
        Button marketBtn  = navBtn("📈", "Market Watch", "MARKET");
        Button tradeBtn   = navBtn("💱", "Trade",        "TRADE");
        Button portfolioBtn=navBtn("💼", "Portfolio",    "PORTFOLIO");

        Label analyticsLbl = navSectionLabel("ANALYTICS");
        Button chartsBtn   = navBtn("📉", "Charts",      "CHARTS");
        Button aiBtn       = navBtn("🤖", "AI Insights", "AI");
        Button leaderBtn   = navBtn("🏆", "Leaderboard", "LEADERBOARD");

        Label toolsLbl     = navSectionLabel("TOOLS");
        Button alertsBtn   = navBtn("🔔", "Price Alerts","ALERTS");
        Button reportsBtn  = navBtn("📄", "Reports",     "REPORTS");

        Label settingsLbl  = navSectionLabel("ACCOUNT");
        Button settingsBtn = navBtn("⚙️", "Settings",   "SETTINGS");
        Button aboutBtn    = navBtn("ℹ️", "About",      "ABOUT");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout
        Button logoutBtn = new Button("🚪  Logout");
        logoutBtn.setStyle("-fx-background-color: rgba(239,68,68,0.1); -fx-text-fill: #ef4444; " +
            "-fx-background-radius: 10; -fx-padding: 10 16; -fx-cursor: hand; " +
            "-fx-alignment: CENTER_LEFT; -fx-pref-width: 190px; -fx-font-size:13px;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: rgba(239,68,68,0.2); -fx-text-fill: #ef4444; " +
            "-fx-background-radius: 10; -fx-padding: 10 16; -fx-cursor: hand; " +
            "-fx-alignment: CENTER_LEFT; -fx-pref-width: 190px; -fx-font-size:13px;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: rgba(239,68,68,0.1); -fx-text-fill: #ef4444; " +
            "-fx-background-radius: 10; -fx-padding: 10 16; -fx-cursor: hand; " +
            "-fx-alignment: CENTER_LEFT; -fx-pref-width: 190px; -fx-font-size:13px;"));
        logoutBtn.setOnAction(e -> logout());

        sidebar.getChildren().addAll(
            logoBox, userChip, sep1,
            mainLbl, dashBtn, marketBtn, tradeBtn, portfolioBtn,
            analyticsLbl, chartsBtn, aiBtn, leaderBtn,
            toolsLbl, alertsBtn, reportsBtn,
            settingsLbl, settingsBtn, aboutBtn,
            spacer, logoutBtn
        );

        return sidebar;
    }

    private Label navSectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #334155; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 12 8 4 8;");
        return lbl;
    }

    private Button navBtn(String icon, String text, String view) {
        Button btn = new Button(icon + "  " + text);
        btn.getStyleClass().add("nav-btn");
        btn.setOnAction(e -> {
            setActiveBtn(btn);
            navigateTo(view);
        });
        btn.setOnMouseEntered(e -> {
            if (btn != activeNavBtn)
                btn.setStyle("-fx-background-color: #1e2436; -fx-text-fill: #e8eaed; " +
                    "-fx-font-size:13px; -fx-alignment:CENTER_LEFT; -fx-padding:10 16; " +
                    "-fx-background-radius:10; -fx-cursor:hand; -fx-pref-width:190px;");
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeNavBtn)
                btn.setStyle("");
        });
        return btn;
    }

    private void setActiveBtn(Button btn) {
        if (activeNavBtn != null) {
            activeNavBtn.getStyleClass().remove("nav-btn-active");
            activeNavBtn.getStyleClass().add("nav-btn");
        }
        activeNavBtn = btn;
        btn.getStyleClass().remove("nav-btn");
        btn.getStyleClass().add("nav-btn-active");
    }

    // ─── Ticker Bar ───────────────────────────────────────────────────────────

    private HBox buildTickerBar() {
        StockTickerBar ticker = new StockTickerBar();
        return ticker.getRoot();
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    private void navigateTo(String view) {
        Parent content = switch (view) {
            case "DASHBOARD"  -> { if (dashboardView == null)  dashboardView  = new DashboardView();  yield dashboardView.getRoot(); }
            case "MARKET"     -> { if (marketView == null)     marketView     = new MarketView();      yield marketView.getRoot(); }
            case "TRADE"      -> { MarketView mv = marketView != null ? marketView : new MarketView(); yield mv.getRoot(); }
            case "PORTFOLIO"  -> { if (portfolioView == null)  portfolioView  = new PortfolioView();  yield portfolioView.getRoot(); }
            case "CHARTS"     -> { if (chartsView == null)     chartsView     = new ChartsView();      yield chartsView.getRoot(); }
            case "AI"         -> { if (aiView == null)         aiView         = new AIInsightsView();  yield aiView.getRoot(); }
            case "LEADERBOARD"-> { if (leaderView == null)     leaderView     = new LeaderboardView(); yield leaderView.getRoot(); }
            case "ALERTS"     -> { if (alertsView == null)     alertsView     = new AlertsView();      yield alertsView.getRoot(); }
            case "REPORTS"    -> { if (reportsView == null)    reportsView    = new ReportsView();      yield reportsView.getRoot(); }
            case "SETTINGS"   -> { if (settingsView == null)   settingsView   = new SettingsView();    yield settingsView.getRoot(); }
            case "ABOUT"      -> { if (aboutView == null)      aboutView      = new AboutView();        yield aboutView.getRoot(); }
            default            -> new Label("View not found: " + view);
        };

        contentPane.getChildren().setAll(content);
        AppController.getInstance().setCurrentView(view);

        // Fade-in
        FadeTransition ft = new FadeTransition(Duration.millis(250), content);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Are you sure you want to logout?", ButtonType.YES, ButtonType.CANCEL);
        confirm.setTitle("Logout – TradeSphere Pro");
        confirm.setHeaderText("Logout Confirmation");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                AppController.getInstance().logout();
                Stage stage = App.getPrimaryStage();
                LoginScreen login = new LoginScreen();
                stage.getScene().setRoot(login.getRoot());
                stage.setMaximized(false);
                stage.setWidth(1280); stage.setHeight(760);
                stage.centerOnScreen();
                FadeTransition ft = new FadeTransition(Duration.millis(400), login.getRoot());
                ft.setFromValue(0); ft.setToValue(1); ft.play();
            }
        });
    }

    public Parent getRoot() { return root; }
}
