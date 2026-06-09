package com.tradesphere.view;

import com.tradesphere.App;
import com.tradesphere.services.ThemeManager;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

/**
 * Premium animated landing page.
 * Inspired by TradingView / Robinhood landing pages.
 */
public class LandingScreen {

    private final StackPane root = new StackPane();
    private final Random random = new Random();

    public LandingScreen() {
        build();
    }

    private void build() {
        root.getStyleClass().add("landing-root");

        // ── Animated canvas background ─────────────────────────────────────
        Canvas canvas = new Canvas(1280, 760);
        drawAnimatedBg(canvas);

        // ── Content overlay ────────────────────────────────────────────────
        ScrollPane scroll = new ScrollPane(buildContent());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.getStylesheets().add("data:text/css,.scroll-bar{-fx-background-color:transparent;}");

        root.getChildren().addAll(canvas, scroll);
    }

    // ─── Content ──────────────────────────────────────────────────────────────

    private VBox buildContent() {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: transparent;");
        content.getChildren().addAll(
            buildNavBar(),
            buildHero(),
            buildStats(),
            buildFeatures(),
            buildCTA(),
            buildFooter()
        );
        return content;
    }

    // ─── NavBar ──────────────────────────────────────────────────────────────

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(18, 48, 18, 48));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setSpacing(0);
        nav.setStyle("-fx-background-color: rgba(13,15,20,0.85);");

        // Logo
        VBox logoBox = new VBox(0);
        Label logo = new Label("🌐 TradeSphere Pro");
        logo.getStyleClass().add("sidebar-logo");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        Label tagline = new Label("Learn, Trade, Analyze.");
        tagline.setStyle("-fx-font-size:10px; -fx-text-fill:#64748b;");
        logoBox.getChildren().addAll(logo, tagline);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Nav links
        HBox links = new HBox(8);
        links.setAlignment(Pos.CENTER);
        String[] navItems = {"Market", "Features", "About"};
        for (String item : navItems) {
            Button btn = new Button(item);
            btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#94a3b8; -fx-font-size:13px; -fx-cursor:hand; -fx-padding:6 14;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#e8eaed; -fx-font-size:13px; -fx-cursor:hand; -fx-padding:6 14;"));
            btn.setOnMouseExited(e ->  btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#94a3b8; -fx-font-size:13px; -fx-cursor:hand; -fx-padding:6 14;"));
            links.getChildren().add(btn);
        }

        // Theme toggle
        Button themeBtn = new Button("🌙");
        themeBtn.setStyle("-fx-background-color:#1e2436; -fx-text-fill:#94a3b8; -fx-background-radius:20; -fx-padding:6 12; -fx-cursor:hand;");
        themeBtn.setOnAction(e -> ThemeManager.getInstance().toggleTheme());

        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("btn-outline");
        loginBtn.setOnAction(e -> navigateToLogin());

        Button startBtn = new Button("Start Trading →");
        startBtn.getStyleClass().add("btn-primary");
        startBtn.setOnAction(e -> navigateToLogin());

        links.getChildren().addAll(themeBtn, loginBtn, startBtn);

        nav.getChildren().addAll(logoBox, spacer, links);
        return nav;
    }

    // ─── Hero Section ────────────────────────────────────────────────────────

    private VBox buildHero() {
        VBox hero = new VBox(24);
        hero.setPadding(new Insets(100, 80, 100, 80));
        hero.setAlignment(Pos.CENTER);
        hero.setStyle("-fx-background-color: transparent;");

        // Badge
        Label badge = new Label("🚀  Virtual Trading Platform v1.0");
        badge.setStyle("-fx-background-color:rgba(59,130,246,0.15); -fx-text-fill:#3b82f6; " +
            "-fx-background-radius:20; -fx-padding:6 16; -fx-font-size:12px; -fx-font-weight:bold;");

        // Main title with gradient effect via multi-label trick
        VBox titleBox = new VBox(4);
        titleBox.setAlignment(Pos.CENTER);

        Label title1 = new Label("Trade Smarter,");
        title1.setFont(Font.font("Segoe UI", FontWeight.BOLD, 62));
        title1.setStyle("-fx-text-fill: #e8eaed;");

        Label title2 = new Label("Grow Faster.");
        title2.setFont(Font.font("Segoe UI", FontWeight.BOLD, 62));
        title2.setStyle("-fx-text-fill: #3b82f6;");

        titleBox.getChildren().addAll(title1, title2);

        Label subtitle = new Label("TradeSphere Pro gives you a fully simulated stock trading experience\n" +
            "with real-time price movements, portfolio analytics, and AI insights.");
        subtitle.setFont(Font.font("Segoe UI", 16));
        subtitle.setStyle("-fx-text-fill: #94a3b8;");
        subtitle.setTextAlignment(TextAlignment.CENTER);
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(640);

        // Buttons
        HBox buttons = new HBox(16);
        buttons.setAlignment(Pos.CENTER);

        Button startBtn = new Button("🚀  Start Trading Now");
        startBtn.getStyleClass().add("btn-primary");
        startBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        startBtn.setPadding(new Insets(14, 36, 14, 36));
        startBtn.setOnAction(e -> navigateToLogin());

        Button exploreBtn = new Button("📊  Explore Market");
        exploreBtn.getStyleClass().add("btn-secondary");
        exploreBtn.setFont(Font.font("Segoe UI", 15));
        exploreBtn.setPadding(new Insets(14, 36, 14, 36));
        exploreBtn.setOnAction(e -> navigateToLogin());

        buttons.getChildren().addAll(startBtn, exploreBtn);

        // Trust indicators
        HBox trust = new HBox(32);
        trust.setAlignment(Pos.CENTER);
        String[][] badges = {{"20", "Stocks"}, {"₹1L", "Starting Cash"}, {"0%", "Commission"}, {"24/7", "Simulation"}};
        for (String[] b : badges) {
            VBox item = new VBox(2);
            item.setAlignment(Pos.CENTER);
            Label val = new Label(b[0]);
            val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
            val.setStyle("-fx-text-fill:#3b82f6;");
            Label lbl = new Label(b[1]);
            lbl.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px;");
            item.getChildren().addAll(val, lbl);
            trust.getChildren().add(item);
        }

        hero.getChildren().addAll(badge, titleBox, subtitle, buttons, trust);

        // Fade-in animation
        FadeTransition fade = new FadeTransition(Duration.millis(1000), hero);
        fade.setFromValue(0); fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(800), hero);
        slide.setFromY(40); slide.setToY(0);
        new ParallelTransition(fade, slide).play();

        return hero;
    }

    // ─── Stats Section ───────────────────────────────────────────────────────

    private HBox buildStats() {
        HBox stats = new HBox();
        stats.setPadding(new Insets(40, 80, 40, 80));
        stats.setAlignment(Pos.CENTER);
        stats.setSpacing(0);
        stats.setStyle("-fx-background-color: rgba(20,24,32,0.9);");

        Object[][] data = {
            {"50,000+", "Active Users",    "👥"},
            {"2M+",     "Total Trades",    "📈"},
            {"₹500Cr+", "Volume Simulated","💰"},
            {"20",      "Listed Stocks",   "🏢"},
        };

        for (int i = 0; i < data.length; i++) {
            VBox item = new VBox(6);
            item.setAlignment(Pos.CENTER);
            item.setPadding(new Insets(20, 60, 20, 60));

            Label icon = new Label((String) data[i][2]);
            icon.setFont(Font.font(28));

            Label value = new Label((String) data[i][0]);
            value.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
            value.setStyle("-fx-text-fill: #3b82f6;");

            // Counting animation
            animateCounter(value, (String) data[i][0]);

            Label label = new Label((String) data[i][1]);
            label.setStyle("-fx-text-fill: #64748b; -fx-font-size:13px;");

            item.getChildren().addAll(icon, value, label);

            if (i < data.length - 1) {
                Separator sep = new Separator(Orientation.VERTICAL);
                sep.setStyle("-fx-background-color: #252d42; -fx-pref-height:60;");
                stats.getChildren().add(item);
                stats.getChildren().add(sep);
            } else {
                stats.getChildren().add(item);
            }
        }

        return stats;
    }

    private void animateCounter(Label label, String finalValue) {
        // Simple pulse animation to simulate counting
        ScaleTransition st = new ScaleTransition(Duration.millis(600), label);
        st.setFromX(0.5); st.setFromY(0.5);
        st.setToX(1.0);   st.setToY(1.0);
        st.play();
    }

    // ─── Features Section ────────────────────────────────────────────────────

    private VBox buildFeatures() {
        VBox section = new VBox(40);
        section.setPadding(new Insets(80, 80, 80, 80));
        section.setAlignment(Pos.CENTER);
        section.setStyle("-fx-background-color: transparent;");

        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        Label title = new Label("Everything You Need to Trade Like a Pro");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 34));
        title.setStyle("-fx-text-fill: #e8eaed;");
        Label sub = new Label("Packed with professional tools — completely free.");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size:15px;");
        header.getChildren().addAll(title, sub);

        // Feature grid 3×2
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        Object[][] features = {
            {"📈", "Virtual Trading",      "Buy & sell 20 real-world stocks with ₹1,00,000 virtual cash. Zero risk, real experience."},
            {"💼", "Portfolio Analytics",   "Track your holdings, P&L, and ROI in real time. Know your performance at a glance."},
            {"🔍", "Market Watch",          "Monitor live price movements, day high/low, volume, and market cap for all stocks."},
            {"⚡", "Real-Time Simulation",  "Multithreaded market engine simulates price ticks every 2 seconds with Brownian motion."},
            {"🤖", "AI Insights",           "Smart BUY/HOLD/SELL signals powered by SMA crossover and momentum analysis."},
            {"🌙", "Dark & Light Mode",     "Switch between stunning dark and light themes instantly with persistent preference."},
        };

        for (int i = 0; i < features.length; i++) {
            VBox card = buildFeatureCard(
                (String)features[i][0], (String)features[i][1], (String)features[i][2]);
            grid.add(card, i % 3, i / 3);
            // Staggered entrance
            final int delay = i * 100;
            FadeTransition ft = new FadeTransition(Duration.millis(600), card);
            ft.setDelay(Duration.millis(delay));
            ft.setFromValue(0); ft.setToValue(1);
            TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
            tt.setDelay(Duration.millis(delay));
            tt.setFromY(30); tt.setToY(0);
            new ParallelTransition(ft, tt).play();
        }

        section.getChildren().addAll(header, grid);
        return section;
    }

    private VBox buildFeatureCard(String icon, String title, String desc) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(24));
        card.setMinWidth(340);
        card.setMaxWidth(380);
        card.setStyle("-fx-background-color: rgba(26,31,46,0.85); -fx-background-radius:16; " +
            "-fx-border-color: rgba(59,130,246,0.15); -fx-border-width:1; -fx-border-radius:16; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 2);");

        Label ico = new Label(icon);
        ico.setFont(Font.font(34));

        Label t = new Label(title);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        t.setStyle("-fx-text-fill: #e8eaed;");

        Label d = new Label(desc);
        d.setStyle("-fx-text-fill: #94a3b8; -fx-font-size:13px;");
        d.setWrapText(true);
        d.setMaxWidth(340);

        card.getChildren().addAll(ico, t, d);

        // Hover glow
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: rgba(30,36,54,0.95); -fx-background-radius:16; " +
            "-fx-border-color: rgba(59,130,246,0.5); -fx-border-width:1.5; -fx-border-radius:16; " +
            "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.25), 20, 0, 0, 4); -fx-scale-x:1.02; -fx-scale-y:1.02;"));
        card.setOnMouseExited(e  -> card.setStyle(
            "-fx-background-color: rgba(26,31,46,0.85); -fx-background-radius:16; " +
            "-fx-border-color: rgba(59,130,246,0.15); -fx-border-width:1; -fx-border-radius:16; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 2);"));

        return card;
    }

    // ─── CTA Banner ──────────────────────────────────────────────────────────

    private VBox buildCTA() {
        VBox cta = new VBox(20);
        cta.setPadding(new Insets(80, 80, 80, 80));
        cta.setAlignment(Pos.CENTER);
        cta.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1e3a5f, #0d1b2e);");

        Label title = new Label("Ready to Start Your Trading Journey?");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #e8eaed;");

        Label sub = new Label("Create your free account and get ₹1,00,000 virtual cash instantly.");
        sub.setStyle("-fx-text-fill: #94a3b8; -fx-font-size:15px;");

        Button btn = new Button("🚀  Create Free Account");
        btn.getStyleClass().add("btn-primary");
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btn.setPadding(new Insets(16, 48, 16, 48));
        btn.setOnAction(e -> navigateToLogin());

        cta.getChildren().addAll(title, sub, btn);
        return cta;
    }

    // ─── Footer ──────────────────────────────────────────────────────────────

    private VBox buildFooter() {
        VBox footer = new VBox(16);
        footer.setPadding(new Insets(40, 80, 40, 80));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: #080a0f;");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label brand = new Label("🌐 TradeSphere Pro");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brand.setStyle("-fx-text-fill: #3b82f6;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label dev = new Label("Developed by  MOHAMMAD SAKIB AHMAD");
        dev.setStyle("-fx-text-fill: #64748b; -fx-font-size:12px;");

        top.getChildren().addAll(brand, sp, dev);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1e2436;");

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        Label copy = new Label("© 2026 TradeSphere Pro  •  Version 1.0  •  For Educational Purposes Only");
        copy.setStyle("-fx-text-fill: #334155; -fx-font-size:12px;");
        bottom.getChildren().add(copy);

        footer.getChildren().addAll(top, sep, bottom);
        return footer;
    }

    // ─── Background Canvas Animation ─────────────────────────────────────────

    private void drawAnimatedBg(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // Dark gradient background
        LinearGradient grad = new LinearGradient(0,0,1,1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#0a0c12")),
            new Stop(1, Color.web("#0d1b35")));

        // Animate floating particles
        double[] px = new double[40], py = new double[40], pvy = new double[40];
        for (int i = 0; i < px.length; i++) {
            px[i] = random.nextDouble() * 1280;
            py[i] = random.nextDouble() * 760;
            pvy[i] = (random.nextDouble() - 0.5) * 0.4;
        }

        AnimationTimer timer = new AnimationTimer() {
            long last = 0;
            @Override public void handle(long now) {
                if (now - last < 50_000_000) return;
                last = now;
                gc.save();
                gc.setFill(new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#0a0c12")),
                    new Stop(1, Color.web("#0d1b35"))));
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Draw floating particles
                for (int i = 0; i < px.length; i++) {
                    py[i] += pvy[i];
                    if (py[i] < 0) py[i] = canvas.getHeight();
                    if (py[i] > canvas.getHeight()) py[i] = 0;
                    gc.setFill(Color.rgb(59, 130, 246, 0.15));
                    gc.fillOval(px[i], py[i], 3, 3);
                }

                // Draw glowing orbs
                gc.setFill(new RadialGradient(0,0,0.3,0.3,1,true,CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(59,130,246,0.08)),
                    new Stop(1, Color.rgb(59,130,246,0))));
                gc.fillOval(-200, -200, 700, 700);
                gc.setFill(new RadialGradient(0,0,0.7,0.7,0.5,true,CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(139,92,246,0.06)),
                    new Stop(1, Color.rgb(139,92,246,0))));
                gc.fillOval(900, 400, 600, 600);

                gc.restore();
            }
        };
        timer.start();
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    private void navigateToLogin() {
        Stage stage = App.getPrimaryStage();
        LoginScreen login = new LoginScreen();
        stage.getScene().setRoot(login.getRoot());

        FadeTransition ft = new FadeTransition(Duration.millis(400), login.getRoot());
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    public Parent getRoot() { return root; }
}
