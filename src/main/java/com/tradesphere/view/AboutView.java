package com.tradesphere.view;

import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

/**
 * About page: developer info, features, future scope.
 */
public class AboutView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);

    public AboutView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");
        buildContent();
        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#0d0f14; -fx-background:#0d0f14;");
    }

    private void buildContent() {
        // Hero card
        VBox heroCard = new VBox(16);
        heroCard.setPadding(new Insets(40));
        heroCard.setAlignment(Pos.CENTER);
        heroCard.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #0d1b35, #1a2744);" +
            "-fx-background-radius:20; -fx-border-color:rgba(59,130,246,0.3); -fx-border-width:1; -fx-border-radius:20;" +
            "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 20, 0, 0, 4);");

        Label logo = new Label("🌐");
        logo.setFont(Font.font(56));

        Label appName = new Label("TradeSphere Pro");
        appName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        appName.setStyle("-fx-text-fill:#e8eaed;");

        Label tagline = new Label("\"Learn, Trade, Analyze.\"");
        tagline.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 16));
        tagline.setStyle("-fx-text-fill:#3b82f6;");

        Label version = new Label("Version 1.0  •  2026");
        version.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px;");

        heroCard.getChildren().addAll(logo, appName, tagline, version);

        // Two-column layout
        HBox mainRow = new HBox(20);

        VBox leftCol  = new VBox(20);
        VBox rightCol = new VBox(20);
        HBox.setHgrow(leftCol,  Priority.ALWAYS);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(buildDeveloperCard(), buildTechStackCard());
        rightCol.getChildren().addAll(buildFeaturesCard(), buildFutureCard());

        mainRow.getChildren().addAll(leftCol, rightCol);

        content.getChildren().addAll(heroCard, mainRow, buildVivaCard());
    }

    // ─── Developer ────────────────────────────────────────────────────────────

    private VBox buildDeveloperCard() {
        VBox card = infoCard("👨‍💻 Developer");
        String[][] info = {
            {"Name",    "Mohammad Sakib Ahmad"},
            {"Project", "TradeSphere Pro – Virtual Stock Trading Platform"},
            {"Course",  "B.Tech – Computer Science & Engineering"},
            {"Year",    "Final Year, 2026"},
            {"Email",   "sakib@tradesphere.pro"},
            {"GitHub",  "github.com/MohammadSakibAhmad0874"},
        };
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(10);
        for (int i = 0; i < info.length; i++) {
            Label key = new Label(info[i][0]);
            key.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px; -fx-font-weight:bold; -fx-min-width:80;");
            Label val = new Label(info[i][1]);
            val.setStyle("-fx-text-fill:#e8eaed; -fx-font-size:13px;");
            grid.add(key, 0, i); grid.add(val, 1, i);
        }
        card.getChildren().add(grid);
        return card;
    }

    // ─── Tech Stack ───────────────────────────────────────────────────────────

    private VBox buildTechStackCard() {
        VBox card = infoCard("🛠 Technology Stack");
        String[][] techs = {
            {"☕", "Java 21",           "Core language"},
            {"🎨", "JavaFX 21",         "UI framework"},
            {"🗄", "SQLite",            "Database persistence"},
            {"📦", "Gson 2.10",         "JSON serialization"},
            {"📄", "Apache PDFBox 3",   "PDF report generation"},
            {"⚡", "ScheduledExecutor", "Market simulation"},
            {"🎯", "MVC Pattern",       "Application architecture"},
        };
        VBox list = new VBox(8);
        for (String[] t : techs) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 12, 8, 12));
            row.setStyle("-fx-background-color:rgba(255,255,255,0.03); -fx-background-radius:8;");
            Label ico  = new Label(t[0]); ico.setStyle("-fx-font-size:16px;");
            Label name = new Label(t[1]); name.setStyle("-fx-text-fill:#e8eaed; -fx-font-weight:bold; -fx-min-width:140;");
            Label desc = new Label(t[2]); desc.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");
            row.getChildren().addAll(ico, name, desc);
            list.getChildren().add(row);
        }
        card.getChildren().add(list);
        return card;
    }

    // ─── Features ─────────────────────────────────────────────────────────────

    private VBox buildFeaturesCard() {
        VBox card = infoCard("✨ Key Features");
        String[] features = {
            "🔐 Complete Authentication System (Login / Register / Forgot Password)",
            "📈 20 Real-World Stocks with Live Price Simulation",
            "💱 Full Buy & Sell Engine with Balance Validation",
            "💼 Portfolio Management with Holdings & P&L",
            "📊 4 Interactive Canvas Charts (Line, Pie, Bar)",
            "🤖 AI BUY/HOLD/SELL Signals with Confidence Scores",
            "🏆 Trader Leaderboard with Rankings",
            "🔔 Price Alert System with Popup Notifications",
            "📄 Report Export: CSV, TXT, PDF",
            "🌙 Dark & Light Theme with Persistent Preference",
            "🎫 Real-Time Animated Ticker Bar",
            "📝 Activity Log with Complete Audit Trail",
            "🗄 SQLite Database with All Data Persisted",
        };
        VBox list = new VBox(6);
        for (String f : features) {
            Label l = new Label(f);
            l.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");
            l.setWrapText(true);
            list.getChildren().add(l);
        }
        card.getChildren().add(list);
        return card;
    }

    // ─── Future Scope ─────────────────────────────────────────────────────────

    private VBox buildFutureCard() {
        VBox card = infoCard("🚀 Future Scope");
        String[][] future = {
            {"🌐", "REST API Integration",  "Connect to Alpha Vantage / NSE / BSE for live data"},
            {"📡", "WebSocket Feed",        "Real-time streaming via WebSocket protocol"},
            {"📱", "Mobile App",            "Android/iOS companion app via React Native"},
            {"🤖", "ML Predictions",        "LSTM model for price forecasting"},
            {"👥", "Social Trading",        "Copy trades from top performers"},
            {"💹", "Options & Futures",     "Derivatives trading simulation"},
            {"🏦", "Multi-Account",         "Support multiple portfolios per user"},
            {"📧", "Email Alerts",          "Real email notifications via SMTP"},
        };
        VBox list = new VBox(8);
        for (String[] f : future) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label ico  = new Label(f[0]); ico.setStyle("-fx-font-size:16px;");
            VBox info = new VBox(1);
            Label name = new Label(f[1]); name.setStyle("-fx-text-fill:#e8eaed; -fx-font-size:12px; -fx-font-weight:bold;");
            Label desc = new Label(f[2]); desc.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px;");
            info.getChildren().addAll(name, desc);
            row.getChildren().addAll(ico, info);
            list.getChildren().add(row);
        }
        card.getChildren().add(list);
        return card;
    }

    // ─── Viva Q&A ─────────────────────────────────────────────────────────────

    private VBox buildVivaCard() {
        VBox card = infoCard("🎓 Viva Questions & Answers");
        Object[][] qa = {
            {"Q1. What is the main purpose of TradeSphere Pro?",
             "A. TradeSphere Pro is a virtual stock trading simulation platform that allows users to " +
             "practice trading without real financial risk, using ₹1,00,000 virtual cash."},
            {"Q2. Which design pattern is used?",
             "A. MVC (Model-View-Controller) pattern is used. Models hold data, Views display UI, " +
             "Controllers handle business logic and coordinate between them."},
            {"Q3. How is real-time simulation implemented?",
             "A. A ScheduledExecutorService runs a background thread that updates stock prices " +
             "every 2 seconds using Brownian motion with sector-based volatility."},
            {"Q4. How is data persisted?",
             "A. SQLite database via JDBC stores all users, holdings, transactions, alerts, and logs. " +
             "The DatabaseManager singleton handles all CRUD operations."},
            {"Q5. What is the AI Insights feature?",
             "A. Rule-based signals using SMA5/SMA20 crossover, RSI-like momentum, and day-range " +
             "position to generate BUY/HOLD/SELL with a confidence score (0-100%)."},
            {"Q6. How are reports generated?",
             "A. CSV and TXT via Java's PrintWriter, PDF via Apache PDFBox with professional " +
             "formatting, tables, color-coded P&L, and footer branding."},
            {"Q7. How is threading handled safely?",
             "A. All background price updates are posted back to the JavaFX Application Thread " +
             "via Platform.runLater() to prevent cross-thread UI modification."},
            {"Q8. What OOP concepts are demonstrated?",
             "A. Encapsulation (private fields + getters), Inheritance (TableCell subclasses), " +
             "Polymorphism (TradeResult record), Abstraction (service interfaces)."},
        };
        VBox list = new VBox(16);
        for (Object[] item : qa) {
            VBox qBox = new VBox(4);
            qBox.setPadding(new Insets(12));
            qBox.setStyle("-fx-background-color:rgba(255,255,255,0.03); -fx-background-radius:8;");
            Label q = new Label((String)item[0]);
            q.setStyle("-fx-text-fill:#3b82f6; -fx-font-weight:bold; -fx-font-size:13px;");
            q.setWrapText(true);
            Label a = new Label((String)item[1]);
            a.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");
            a.setWrapText(true);
            qBox.getChildren().addAll(q, a);
            list.getChildren().add(qBox);
        }
        card.getChildren().add(list);
        return card;
    }

    private VBox infoCard(String title) {
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

    public Parent getRoot() { return root; }
}
