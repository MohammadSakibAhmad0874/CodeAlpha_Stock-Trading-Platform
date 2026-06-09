# TradeSphere Pro — UML Diagrams

---

## 1. Class Diagram (Core Domain)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        TradeSphere Pro Class Diagram                        │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────────┐         ┌──────────────────────┐
│      User        │ 1     * │      Transaction      │
│──────────────────│─────────│──────────────────────│
│ -id: String      │         │ -id: String           │
│ -fullName: String│         │ -userId: String       │
│ -email: String   │         │ -stockSymbol: String  │
│ -username: String│         │ -quantity: int        │
│ -passwordHash    │         │ -price: double        │
│ -walletBalance   │         │ -buy: boolean         │
│ -theme: String   │         │ -timestamp: String    │
│ -currency: String│         │──────────────────────│
│──────────────────│         │ +getTotalAmount()     │
│ +canAfford()     │         │ +isBuy(): boolean     │
│ +debit()         │         └──────────────────────┘
│ +credit()        │
│ +recordTrade()   │         ┌──────────────────────┐
└────────┬─────────┘ 1     1 │      Portfolio        │
         │                   │──────────────────────│
         │                   │ -userId: String       │
         │owns               │ -holdings: Map<>      │
         │                   │──────────────────────│
         │                   │ +buy()                │
         ▼                   │ +sell()               │
┌──────────────────┐         │ +getTotalValue()      │
│   PriceAlert     │         │ +getProfitOrLoss()    │
│──────────────────│         └──────────────────────┘
│ -stockSymbol     │                    │
│ -condition: enum │                    │ contains *
│ -targetPrice     │                    ▼
│ -status: enum    │         ┌──────────────────────┐
│──────────────────│         │   Holding (nested)    │
│ +isTriggered()   │         │──────────────────────│
└──────────────────┘         │ -symbol: String       │
                             │ -quantity: int        │
┌──────────────────┐         │ -avgBuyPrice: double  │
│     Stock        │         │ -currentPrice: double │
│──────────────────│         │──────────────────────│
│ -symbol: String  │         │ +getProfitOrLoss()    │
│ -companyName     │         │ +getReturnPct()       │
│ -currentPrice    │◄────────└──────────────────────┘
│ -dayHigh/Low     │  references
│ -volume: long    │
│ -sector: String  │
│ -priceHistory[]  │
│──────────────────│
│ +getPriceHistory │
│ JavaFX Properties│
└──────────────────┘
```

---

## 2. MVC Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     MVC Architecture                            │
└─────────────────────────────────────────────────────────────────┘

         ┌──────────────────────────────────────┐
         │              VIEW LAYER              │
         │  LandingScreen  │  LoginScreen       │
         │  DashboardView  │  MarketView        │
         │  PortfolioView  │  ChartsView        │
         │  AIInsightsView │  LeaderboardView   │
         │  ReportsView    │  AlertsView        │
         │  SettingsView   │  AboutView         │
         └──────────┬───────────────────────────┘
                    │ calls
         ┌──────────▼───────────────────────────┐
         │           CONTROLLER LAYER           │
         │  AppController  (session/state)      │
         │  AuthController (login/register)     │
         │  TradeController (buy/sell)          │
         │  AlertController (alerts)            │
         └──────────┬───────────────────────────┘
                    │ uses
         ┌──────────▼───────────────────────────┐
         │           SERVICE LAYER              │
         │  MarketSimulator  (price engine)     │
         │  AIInsightsService (signals)         │
         │  NotificationManager (toasts)        │
         │  ThemeManager (CSS switch)           │
         │  ReportGenerator (CSV/TXT/PDF)       │
         └──────────┬───────────────────────────┘
                    │ persists
         ┌──────────▼───────────────────────────┐
         │        DATABASE / MODEL LAYER        │
         │  DatabaseManager  (SQLite JDBC)      │
         │  User, Stock, Portfolio, Transaction │
         │  PriceAlert, ActivityLog, Wallet     │
         └──────────────────────────────────────┘
```

---

## 3. Sequence Diagram — Buy Stock

```
User        MarketView     TradeController   DatabaseManager   NotificationMgr
 │               │                │                 │                │
 │──Buy Click───►│                │                 │                │
 │               │──buy(sym,qty)─►│                 │                │
 │               │                │──getStock()────►│                │
 │               │                │◄─Stock─────────│                │
 │               │                │──validateFunds()                 │
 │               │                │──user.debit()                    │
 │               │                │──portfolio.buy()                 │
 │               │                │──saveTransaction()──►│           │
 │               │                │──saveUser()──────────►│          │
 │               │                │──savePortfolio()─────►│          │
 │               │                │──────────────────────success     │
 │               │                │─────────────────────────────────►│
 │               │                │                      notify("Buy OK")
 │               │◄─TradeResult──│                                   │
 │◄──UI Update───│               │                                   │
```

---

## 4. State Diagram — Application Flow

```
    [App Start]
         │
         ▼
   ┌─────────────┐
   │  LandingPage │ ──── "Start Trading" ────►┌──────────────┐
   └─────────────┘                            │  LoginScreen  │
                                              └──────┬───────┘
                                                     │
                                          ┌──────────▼──────────┐
                                          │  Auth: Login/Register│
                                          └──────────┬──────────┘
                                                     │ Success
                                                     ▼
                                          ┌──────────────────────┐
                                          │     MainWindow        │
                                          │  ┌────────────────┐   │
                                          │  │   Sidebar Nav   │   │
                                          │  └────────────────┘   │
                                          │  ┌────────────────┐   │
                                          │  │  Content Pane  │   │
                                          │  │  (swappable)   │   │
                                          │  └────────────────┘   │
                                          └──────────┬────────────┘
                                                     │ Logout
                                                     ▼
                                              [LoginScreen]
```

---

## 5. Component Diagram

```
┌────────────────────────────────────────────────────────┐
│                   TradeSphere Pro                       │
│                                                        │
│  ┌──────────┐  ┌──────────┐  ┌───────────────────┐   │
│  │  JavaFX  │  │  SQLite  │  │  Apache PDFBox    │   │
│  │   UI     │  │    DB    │  │  (PDF Reports)    │   │
│  └────┬─────┘  └────┬─────┘  └─────────┬─────────┘   │
│       │              │                  │              │
│  ┌────▼──────────────▼──────────────────▼──────────┐  │
│  │              Core Application                    │  │
│  │   MarketSimulator ──► ScheduledExecutorService   │  │
│  │   DatabaseManager ──► JDBC ──► tradesphere.db    │  │
│  │   ThemeManager    ──► CSS Files                  │  │
│  │   ReportGenerator ──► /reports/ directory        │  │
│  └───────────────────────────────────────────────┘  │
│                                                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐   │
│  │  Gson    │  │  Java IO │  │  Java Concurrent  │   │
│  │  (JSON)  │  │  (Files) │  │  (Threading)      │   │
│  └──────────┘  └──────────┘  └──────────────────┘   │
└────────────────────────────────────────────────────────┘
```
