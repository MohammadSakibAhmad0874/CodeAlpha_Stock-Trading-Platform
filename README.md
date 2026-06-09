# 🌐 TradeSphere Pro — Virtual Stock Trading Platform

> **"Learn, Trade, Analyze."**
>
> A professional-grade desktop trading simulation platform built with Java 21 + JavaFX 21.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://java.com)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)](https://openjfx.io)
[![SQLite](https://img.shields.io/badge/SQLite-3.45-green)](https://sqlite.org)
[![License](https://img.shields.io/badge/License-Educational-purple)](LICENSE)

---

## 📸 Screenshots

| Landing Page | Dashboard | Market Watch |
|---|---|---|
| Animated hero with floating particles | Live portfolio widgets | Real-time stock table |

---

## 🚀 Quick Start

### Prerequisites
| Tool | Version |
|------|---------|
| Java JDK | 21+ |
| Maven | 3.9+ |

### Run
```bash
cd "TradeSphereP ro"
mvn javafx:run
```

### Build Fat JAR
```bash
mvn clean package
java -jar target/tradesphere-pro-1.0.0.jar
```

---

## 📁 Project Structure

```
src/main/java/com/tradesphere/
├── App.java                        # Entry point
├── model/
│   ├── User.java                   # User account model
│   ├── Stock.java                  # Stock with JavaFX properties
│   ├── Transaction.java            # Buy/Sell record
│   ├── Portfolio.java              # Holdings map + P&L
│   ├── Wallet.java                 # Cash snapshot
│   ├── PriceAlert.java             # Price trigger model
│   └── ActivityLog.java            # Audit log entry
├── database/
│   └── DatabaseManager.java        # SQLite CRUD singleton
├── services/
│   ├── MarketSimulator.java        # ScheduledExecutor price engine
│   ├── AIInsightsService.java      # SMA/RSI signal generator
│   ├── NotificationManager.java    # Toast popup system
│   ├── ThemeManager.java           # CSS theme switcher
│   └── ReportGenerator.java        # CSV / TXT / PDF export
├── controller/
│   ├── AppController.java          # Session & app state
│   ├── AuthController.java         # Login / Register / Recovery
│   ├── TradeController.java        # Buy / Sell execution
│   └── AlertController.java        # Price alert management
└── view/
    ├── LandingScreen.java           # Animated landing page
    ├── LoginScreen.java             # Auth forms
    ├── MainWindow.java              # Sidebar shell
    ├── DashboardView.java           # Portfolio + market widgets
    ├── MarketView.java              # Live stock table
    ├── PortfolioView.java           # Holdings + analytics
    ├── ChartsView.java              # 4 canvas charts
    ├── AIInsightsView.java          # Signal cards
    ├── LeaderboardView.java         # Trader rankings
    ├── ReportsView.java             # Export panel
    ├── AlertsView.java              # Price alerts CRUD
    ├── SettingsView.java            # Preferences
    ├── AboutView.java               # Credits + Viva Q&A
    └── components/
        └── StockTickerBar.java      # Scrolling ticker

src/main/resources/
├── css/
│   ├── dark-theme.css              # Dark theme (default)
│   └── light-theme.css             # Light theme
└── data/                           # Runtime data directory
```

---

## ✨ Features

| Feature | Details |
|---------|---------|
| 🔐 Authentication | Login · Register · Forgot Password · SHA-256 hashing |
| 📈 20 Live Stocks | AAPL, TSLA, NVDA, GOOGL, AMZN, MSFT, META, NFLX… |
| ⚡ Market Sim | Brownian motion · Sector volatility · 2-second ticks |
| 💱 Trading | Buy/Sell with balance validation · Avg price tracking |
| 💼 Portfolio | Holdings table · P&L · ROI · Analytics cards |
| 📊 4 Charts | Portfolio growth line · Sector pie · P&L bar · Price history |
| 🤖 AI Insights | SMA5/SMA20 crossover · Momentum · Confidence 0–100% |
| 🏆 Leaderboard | Ranked by portfolio value · Gold/Silver/Bronze |
| 🔔 Price Alerts | ABOVE/BELOW triggers · Live monitoring · Toast popups |
| 📄 Reports | CSV · TXT · PDF (Apache PDFBox) · Auto-open on export |
| 🎫 Ticker Bar | Infinite scroll · Live binding via JavaFX properties |
| 🌙 Dark/Light | Instant CSS switch · Persistent preference |
| 🗄 SQLite | All data persisted · WAL mode · CRUD via JDBC |
| 📝 Activity Log | Login · Trades · Alerts · Theme changes |

---

## 🎯 Demo Account

| Field | Value |
|-------|-------|
| Username | `demo` |
| Password | `demo123` |

Or register a new account at the login screen.

---

## 🏗 Architecture

```
┌────────────────────────────────────────────┐
│                  View Layer                │
│  LandingScreen → LoginScreen → MainWindow  │
│  (DashboardView, MarketView, ChartsView…)  │
└──────────────────┬─────────────────────────┘
                   │  calls
┌──────────────────▼─────────────────────────┐
│              Controller Layer              │
│  AppController · AuthController            │
│  TradeController · AlertController         │
└──────────────────┬─────────────────────────┘
                   │  reads/writes
┌──────────────────▼─────────────────────────┐
│            Service + Database Layer        │
│  MarketSimulator · AIInsightsService       │
│  NotificationManager · ThemeManager        │
│  ReportGenerator · DatabaseManager (SQLite)│
└──────────────────┬─────────────────────────┘
                   │  entities
┌──────────────────▼─────────────────────────┐
│                Model Layer                 │
│  User · Stock · Portfolio · Transaction    │
│  PriceAlert · ActivityLog · Wallet         │
└────────────────────────────────────────────┘
```

---

## 🛠 Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| JavaFX | 21.0.5 | UI Framework |
| SQLite JDBC | 3.45.3 | Database |
| Gson | 2.10.1 | JSON |
| Apache PDFBox | 3.0.2 | PDF Export |

---

## 📋 Testing

| Test Case | Expected | Status |
|-----------|----------|--------|
| Register new user | Account created, ₹1L balance | ✅ |
| Login with wrong password | Error message shown | ✅ |
| Buy stock with sufficient funds | Holdings updated, balance reduced | ✅ |
| Buy stock with insufficient funds | Error: Insufficient funds | ✅ |
| Sell more shares than owned | Error: You only own N shares | ✅ |
| Price alert ABOVE triggered | Toast notification shown | ✅ |
| Export Portfolio PDF | File saved to /reports/ | ✅ |
| Theme switch Dark↔Light | CSS applied instantly | ✅ |
| Market simulation | Prices update every 2 seconds | ✅ |
| Demo account auto-creation | Login with demo/demo123 | ✅ |

---

## 👨‍💻 Developer

**Mohammad Sakib Ahmad**
B.Tech — Computer Science & Engineering | Final Year 2026

---

## 📄 License

This project is developed for educational purposes as part of a B.Tech final year project.

© 2026 TradeSphere Pro. All Rights Reserved.
