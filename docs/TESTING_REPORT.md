# TradeSphere Pro — Testing Report

**Project:** TradeSphere Pro v1.0  
**Developer:** MOHAMMAD SAKIB AHMAD  
**Testing Type:** Manual Black-Box Testing + Unit Verification  
**Date:** June 2026

---

## Test Environment

| Component | Version |
|-----------|---------|
| OS | Windows 11 |
| JDK | OpenJDK 21.0.8 (Temurin) |
| JavaFX | 21.0.5 |
| SQLite | 3.45.3 |
| Maven | 3.9.9 |

---

## 1. Authentication Module Tests

| TC# | Test Case | Input | Expected | Result | Status |
|-----|-----------|-------|----------|--------|--------|
| TC01 | Register new user | Valid name/email/user/pass | Account created, ₹1L balance | ✅ Pass | ✅ |
| TC02 | Register duplicate username | Existing username | Error: "Username already taken" | ✅ Pass | ✅ |
| TC03 | Register empty fields | Blank name | Error: "All fields required" | ✅ Pass | ✅ |
| TC04 | Login valid credentials | demo / demo123 | Dashboard loads | ✅ Pass | ✅ |
| TC05 | Login wrong password | demo / wrongpass | Error: "Invalid credentials" | ✅ Pass | ✅ |
| TC06 | Login blank username | "" / demo123 | Error shown | ✅ Pass | ✅ |
| TC07 | Forgot password valid email | demo@tradesphere.pro | Recovery message shown | ✅ Pass | ✅ |
| TC08 | Forgot password invalid email | notexist@x.com | Error: "Email not found" | ✅ Pass | ✅ |
| TC09 | Show/hide password toggle | Click eye icon | Password toggles visibility | ✅ Pass | ✅ |
| TC10 | Demo account auto-creation | First app launch | demo/demo123 works | ✅ Pass | ✅ |

---

## 2. Market Simulation Tests

| TC# | Test Case | Expected | Result | Status |
|-----|-----------|----------|--------|--------|
| TC11 | Market start on launch | Prices update every 2s | ✅ Confirmed | ✅ |
| TC12 | Price change direction | Green/Red arrow in table | ✅ Correct colors | ✅ |
| TC13 | Day High/Low tracking | High≥Current≥Low always | ✅ Verified | ✅ |
| TC14 | Price history array | 30-element circular buffer | ✅ Working | ✅ |
| TC15 | Market stop on app close | Executor shutdown called | ✅ Graceful | ✅ |
| TC16 | Ticker bar scroll | Infinite loop animation | ✅ Smooth | ✅ |
| TC17 | 20 stocks loaded | All symbols appear in table | ✅ All 20 | ✅ |

---

## 3. Trading (Buy/Sell) Tests

| TC# | Test Case | Input | Expected | Result | Status |
|-----|-----------|-------|----------|--------|--------|
| TC18 | Buy with sufficient balance | AAPL, 1 share | Holdings updated, balance reduced | ✅ Pass | ✅ |
| TC19 | Buy insufficient balance | AAPL, 10000 shares | Error: "Insufficient funds" | ✅ Pass | ✅ |
| TC20 | Buy quantity = 0 | qty=0 | Error: "Invalid quantity" | ✅ Pass | ✅ |
| TC21 | Buy negative quantity | qty=-1 | Error: "Invalid quantity" | ✅ Pass | ✅ |
| TC22 | Sell owned shares | 1 share of held stock | Balance increased, holding reduced | ✅ Pass | ✅ |
| TC23 | Sell more than owned | 999 shares (own 1) | Error: "You only own 1 share(s)" | ✅ Pass | ✅ |
| TC24 | Sell stock not owned | Unowned stock | Error: "You do not own..." | ✅ Pass | ✅ |
| TC25 | Transaction recorded | Any buy/sell | Appears in recent trades | ✅ Pass | ✅ |
| TC26 | Holdings avg price | Buy at diff prices | Average correctly calculated | ✅ Pass | ✅ |

---

## 4. Portfolio Module Tests

| TC# | Test Case | Expected | Status |
|-----|-----------|----------|--------|
| TC27 | Holdings table populated | After buy, row appears | ✅ |
| TC28 | P&L calculation | (CurPrice - AvgPrice) × Qty | ✅ Correct |
| TC29 | ROI % calculation | (P&L / Invested) × 100 | ✅ Correct |
| TC30 | Live price refresh | P&L updates on every tick | ✅ |
| TC31 | Total analytics cards | Invested/Value/P&L/ROI accurate | ✅ |

---

## 5. Charts Tests

| TC# | Test Case | Expected | Status |
|-----|-----------|----------|--------|
| TC32 | Portfolio line chart renders | Canvas draws line on startup | ✅ |
| TC33 | Sector pie chart | Shows sectors from holdings | ✅ |
| TC34 | P&L bar chart | Green bars=profit, Red=loss | ✅ |
| TC35 | Stock picker chart | Changing picker redraws line | ✅ |

---

## 6. AI Insights Tests

| TC# | Test Case | Expected | Status |
|-----|-----------|----------|--------|
| TC36 | 20 recommendation cards | One per stock | ✅ |
| TC37 | BUY signal | Score≥65, green badge | ✅ |
| TC38 | SELL signal | Score≤35, red badge | ✅ |
| TC39 | HOLD signal | 36≤score≤64, amber badge | ✅ |
| TC40 | Confidence bar accuracy | Bar width = confidence% | ✅ |

---

## 7. Price Alerts Tests

| TC# | Test Case | Expected | Status |
|-----|-----------|----------|--------|
| TC41 | Create alert ABOVE | Alert appears in table as ACTIVE | ✅ |
| TC42 | Create alert BELOW | Alert appears in table as ACTIVE | ✅ |
| TC43 | Alert triggered ABOVE | When price ≥ target, TRIGGERED shown | ✅ |
| TC44 | Delete alert | Row removed from table | ✅ |
| TC45 | Toast notification on trigger | Popup shown with stock name | ✅ |

---

## 8. Reports Tests

| TC# | Test Case | Expected | Status |
|-----|-----------|----------|--------|
| TC46 | Export Portfolio CSV | File saved to /reports/ | ✅ |
| TC47 | Export Portfolio TXT | File saved, readable format | ✅ |
| TC48 | Export Portfolio PDF | PDF created with PDFBox | ✅ |
| TC49 | Export Transactions CSV | All trades listed | ✅ |
| TC50 | Auto-open exported file | OS opens file after export | ✅ |

---

## 9. Theme & Settings Tests

| TC# | Test Case | Expected | Status |
|-----|-----------|----------|--------|
| TC51 | Switch to Light Mode | CSS changes instantly | ✅ |
| TC52 | Switch to Dark Mode | CSS reverts | ✅ |
| TC53 | Theme persists on restart | Preference saved in DB | ✅ |
| TC54 | Profile update | Name/email saved to DB | ✅ |
| TC55 | Portfolio reset | Balance reset to ₹1,00,000 | ✅ |

---

## 10. Performance Tests

| Metric | Target | Actual | Status |
|--------|--------|--------|--------|
| App startup time | < 5s | ~3.2s | ✅ |
| Market tick delay | 2s | ~2.05s | ✅ |
| UI thread blocking | None | None | ✅ |
| Memory usage (idle) | < 300MB | ~210MB | ✅ |
| DB query time (avg) | < 50ms | ~12ms | ✅ |

---

## Summary

| Category | Total | Pass | Fail |
|----------|-------|------|------|
| Authentication | 10 | 10 | 0 |
| Market Simulation | 7 | 7 | 0 |
| Trading | 9 | 9 | 0 |
| Portfolio | 5 | 5 | 0 |
| Charts | 4 | 4 | 0 |
| AI Insights | 5 | 5 | 0 |
| Price Alerts | 5 | 5 | 0 |
| Reports | 5 | 5 | 0 |
| Theme/Settings | 5 | 5 | 0 |
| **Total** | **55** | **55** | **0** |

**Pass Rate: 100%** ✅

---

*Report generated: June 2026 | TradeSphere Pro v1.0*
