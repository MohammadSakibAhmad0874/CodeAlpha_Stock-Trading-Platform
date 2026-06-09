# TradeSphere Pro — User Manual

**Version:** 1.0 | **Developer:** MOHAMMAD SAKIB AHMAD

---

## Welcome to TradeSphere Pro 🌐

TradeSphere Pro is a virtual stock trading simulation platform that lets you practice buying and selling real-world stocks with **₹1,00,000 virtual cash** — completely risk-free.

---

## Table of Contents

1. [Getting Started](#1-getting-started)
2. [Landing Page](#2-landing-page)
3. [Creating an Account](#3-creating-an-account)
4. [Logging In](#4-logging-in)
5. [Dashboard](#5-dashboard)
6. [Market Watch](#6-market-watch)
7. [Buying Stocks](#7-buying-stocks)
8. [Selling Stocks](#8-selling-stocks)
9. [My Portfolio](#9-my-portfolio)
10. [Charts & Analytics](#10-charts--analytics)
11. [AI Insights](#11-ai-insights)
12. [Leaderboard](#12-leaderboard)
13. [Price Alerts](#13-price-alerts)
14. [Reports](#14-reports)
15. [Settings](#15-settings)
16. [About](#16-about)

---

## 1. Getting Started

1. Double-click `run.bat` to launch the application.
2. The app opens on the **Landing Page**.
3. Click **"Start Trading"** or **"Sign In"** to proceed.

> **Quick Demo:** Use `demo` / `demo123` to log in instantly.

---

## 2. Landing Page

The landing page gives you an overview of TradeSphere Pro:

| Section | Description |
|---------|-------------|
| **Hero** | Main title with Start Trading and Explore Market buttons |
| **Statistics** | Live counters: Users, Trades, Volume, Stocks |
| **Features** | 6 feature cards highlighting platform capabilities |
| **CTA** | Call-to-action to create a free account |
| **Footer** | Version info and developer credits |

---

## 3. Creating an Account

1. Click **"Start Trading"** on the landing page
2. Select the **"Register"** tab
3. Fill in:
   - **Full Name** — Your display name
   - **Email** — Valid email address (unique)
   - **Username** — Your login name (unique)
   - **Password** — Minimum 6 characters
4. Click **"Create Account"**
5. ✅ Account is created with **₹1,00,000 virtual cash**
6. Switch to **"Sign In"** tab and log in

---

## 4. Logging In

1. Enter your **Username** and **Password**
2. Click the 👁 icon to show/hide your password
3. Check **"Remember Me"** to save your login
4. Click **"Sign In →"**

**Forgot Password?**
- Click **"Forgot password?"** link
- Enter your registered email
- A recovery message is displayed (simulated)

---

## 5. Dashboard

The Dashboard is your home screen after login.

### Portfolio Value Card
Shows your total account value (cash + holdings) and overall P&L.

### Market Indices
Live-updated indices: NIFTY 50, SENSEX, NASDAQ, S&P 500, DOW JONES, FTSE 100.

### Watchlist
Displays your top 8 tracked stocks with live prices and change %.

### Recent Trades
Your last 8 buy/sell transactions with timestamps.

### Live Clock
Real-time date and time display in the header.

---

## 6. Market Watch

Navigate to **Market Watch** via the sidebar.

### Stock Table Columns
| Column | Description |
|--------|-------------|
| SYMBOL | Stock ticker (e.g., AAPL) |
| COMPANY | Full company name |
| PRICE | Current live price in ₹ |
| CHANGE% | Day change with ▲/▼ indicator |
| HIGH | Day high price |
| LOW | Day low price |
| VOLUME | Trading volume |
| SECTOR | Business sector |
| ACTION | Buy / Sell buttons |

### Search & Filter
- **Search bar** — Type symbol or company name to filter
- **Filter buttons** — ALL / TOP GAINERS / TOP LOSERS / HIGH VOLUME / by sector

### Quick Trade Panel
At the bottom of Market Watch:
1. Select a stock from the dropdown
2. Enter quantity
3. Click **✅ Buy** or **❌ Sell**

---

## 7. Buying Stocks

### Method 1 — Market Watch Table
1. Find your desired stock in the table
2. Click the green **"Buy"** button in the ACTION column
3. A dialog opens with current price
4. Enter quantity and click **"Confirm Buy"**

### Method 2 — Quick Trade Panel
1. Select stock from dropdown
2. Enter quantity
3. Click **✅ Buy**

### What Happens After Buying
- Your **wallet balance** is reduced
- The stock appears in your **Portfolio → Holdings**
- A **✅ notification** confirms the purchase
- The trade is recorded in **Recent Trades**

> ⚠️ **Minimum balance required** = Quantity × Current Price

---

## 8. Selling Stocks

### Method 1 — Portfolio Table
1. Go to **Portfolio** via sidebar
2. Find the stock in your Holdings table
3. Click the red **"Sell"** button
4. Enter quantity to sell
5. Confirm the dialog

### Method 2 — Market Watch Quick Trade
1. Select a stock you own
2. Enter quantity
3. Click **❌ Sell**

### What Happens After Selling
- Your **wallet balance** increases by (sell price × qty)
- The holding quantity is reduced (or removed if qty = 0)
- **Profit/Loss** is calculated: (Sell Price − Avg Buy Price) × Qty
- Trade is recorded in history

---

## 9. My Portfolio

### Holdings Table
| Column | Description |
|--------|-------------|
| SYMBOL | Stock ticker |
| COMPANY | Company name |
| QTY | Shares you own |
| AVG BUY PRICE | Average price you paid |
| CUR PRICE | Live current price |
| P&L | Profit or Loss in ₹ |
| RETURN % | Percentage return |

### Analytics Cards (Top)
| Card | Description |
|------|-------------|
| Total Invested | Amount spent on stocks |
| Current Value | Current market value of holdings |
| Net P&L | Total profit or loss |
| Overall ROI | Return on investment % |

---

## 10. Charts & Analytics

Four interactive charts powered by Canvas:

| Chart | Description |
|-------|-------------|
| **Portfolio Growth** | Line chart of portfolio value history |
| **Sector Allocation** | Donut pie chart of sectors in portfolio |
| **Stock P&L** | Bar chart showing profit/loss per holding |
| **Stock Price History** | Line chart for any selected stock |

Use the **stock picker dropdown** in the price history chart to view any stock.

---

## 11. AI Insights

Navigate to **🤖 AI Insights** in the sidebar.

Each stock gets a recommendation card showing:

| Element | Description |
|---------|-------------|
| **Signal** | BUY ▲ / SELL ▼ / HOLD ◆ |
| **Confidence** | 0–100% score with progress bar |
| **Current Price** | Live price + change % |
| **Rationale** | Plain-English explanation |
| **Sector** | Business sector badge |

> **Disclaimer:** These are algorithmic signals for educational purposes only, not real financial advice.

**Algorithm:** Uses SMA5/SMA20 crossover, RSI-like momentum, and day-range position.

---

## 12. Leaderboard

Navigate to **🏆 Leaderboard** to see all traders ranked by portfolio value.

| Column | Description |
|--------|-------------|
| # | Rank (🥇🥈🥉 for top 3) |
| TRADER | Full name (★ = you) |
| BALANCE | Wallet cash balance |
| P&L | Total profit/loss |
| ROI | Return on investment % |
| TRADES | Number of trades executed |

Your row is highlighted in **blue**.

---

## 13. Price Alerts

Navigate to **🔔 Price Alerts** to set automatic notifications.

### Creating an Alert
1. Select a stock from the dropdown
2. Choose condition: **"Price rises above (≥)"** or **"Price drops below (≤)"**
3. Enter your target price
4. Click **"🔔 Set Alert"**

### Alert Status
| Status | Meaning |
|--------|---------|
| 🟢 ACTIVE | Watching, not yet triggered |
| 🟡 TRIGGERED | Target price was hit |
| ⚫ DISMISSED | Alert was deleted |

When triggered, a **toast notification** appears automatically.

---

## 14. Reports

Navigate to **📄 Reports** to export your data.

### Report Types
| Report | Formats | Content |
|--------|---------|---------|
| Portfolio Report | CSV, TXT, PDF | Holdings, P&L, ROI |
| Trade History | CSV | All buy/sell transactions |

### How to Export
1. Click the format button (e.g., **"⬇ Export PDF"**)
2. File is saved to the `/reports/` folder
3. File opens automatically in your default viewer

### Recent Files
The **Reports Folder** section shows all generated reports with an **Open** button.

---

## 15. Settings

Navigate to **⚙️ Settings** to customize your experience.

| Section | Options |
|---------|---------|
| **Appearance** | 🌙 Dark Mode / ☀️ Light Mode |
| **Notifications** | Trade confirmations, alerts, milestones |
| **Currency** | INR, USD, EUR, GBP, JPY |
| **Market** | Price update speed, auto-save, animations |
| **Profile** | Update full name and email |
| **Danger Zone** | Reset portfolio to ₹1,00,000 |

> 💾 All settings are saved automatically to the database.

---

## 16. About

Navigate to **ℹ️ About** to view:
- Developer information
- Technology stack
- Full feature list
- Future roadmap
- Viva Questions & Answers (for academic presentations)

---

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Esc` | Close dialogs |
| `Enter` | Submit forms (login, trade) |
| `Ctrl+W` | Close window (triggers logout confirmation) |

---

## Tips & Best Practices

1. **Start with research** — Check AI Insights before trading
2. **Set price alerts** — Never miss a target price
3. **Diversify** — Invest in multiple sectors
4. **Track your ROI** — Monitor the Portfolio analytics cards
5. **Export reports** — Save CSV/PDF for reference
6. **Use filters** — In Market Watch, filter by "TOP GAINERS" for momentum plays

---

*© 2026 TradeSphere Pro — Mohammad Sakib Ahmad — Educational Use Only*
