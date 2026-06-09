# TradeSphere Pro — ER Diagram & Database Schema

---

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     TradeSphere Pro ER Diagram                          │
│                       Database: tradesphere.db (SQLite)                 │
└─────────────────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│        USERS          │
│──────────────────────│
│ PK  id          TEXT  │◄──────────────────────────────┐
│     full_name   TEXT  │                               │
│     email       TEXT  │                               │
│     username    TEXT  │                               │
│     pass_hash   TEXT  │                               │
│     balance     REAL  │                               │
│     initial_bal REAL  │                               │
│     theme       TEXT  │                               │
│     currency    TEXT  │                               │
│     notif_on    INT   │                               │
│     created_at  TEXT  │                               │
│     last_login  TEXT  │                               │
│     total_trades INT  │                               │
│     total_profit REAL │                               │
└──────────────────────┘                               │
          │ 1                                           │ FK
          │                                             │
          │ has many                                    │
          ▼ *                                           │
┌──────────────────────┐                    ┌──────────┴────────────┐
│     TRANSACTIONS      │                    │       HOLDINGS         │
│──────────────────────│                    │───────────────────────│
│ PK  id          TEXT  │                    │ PK  id          TEXT  │
│ FK  user_id     TEXT  │                    │ FK  user_id     TEXT  │
│     stock_symbol TEXT  │                    │     symbol      TEXT  │
│     company_name TEXT  │                    │     company_name TEXT │
│     quantity    INT   │                    │     quantity    INT   │
│     price       REAL  │                    │     avg_price   REAL  │
│     total_amt   REAL  │                    │     cur_price   REAL  │
│     is_buy      INT   │                    │──────────────────────│
│     timestamp   TEXT  │                    │ UNIQUE(user_id,symbol)│
└──────────────────────┘                    └──────────────────────┘
          │ *                                           │
          │ references                                  │
          ▼                                             ▼
┌──────────────────────┐              ┌──────────────────────────┐
│      PRICE_ALERTS     │              │       ACTIVITY_LOGS       │
│──────────────────────│              │──────────────────────────│
│ PK  id          TEXT  │              │ PK  id           TEXT     │
│ FK  user_id     TEXT  │              │ FK  user_id      TEXT     │
│     stock_symbol TEXT  │              │     action       TEXT     │
│     company_name TEXT  │              │     description  TEXT     │
│     condition   TEXT  │              │     timestamp    TEXT     │
│     target_price REAL  │              └──────────────────────────┘
│     status      TEXT  │
│     created_at  TEXT  │
│     triggered_at TEXT │
└──────────────────────┘
```

---

## Database Tables — Full Schema (SQLite DDL)

### users
```sql
CREATE TABLE IF NOT EXISTS users (
    id            TEXT PRIMARY KEY,
    full_name     TEXT NOT NULL,
    email         TEXT UNIQUE NOT NULL,
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    wallet_balance REAL DEFAULT 100000.0,
    initial_balance REAL DEFAULT 100000.0,
    theme         TEXT DEFAULT 'DARK',
    currency      TEXT DEFAULT 'INR',
    notifications_enabled INTEGER DEFAULT 1,
    remember_me   INTEGER DEFAULT 0,
    created_at    TEXT NOT NULL,
    last_login    TEXT,
    total_trades  INTEGER DEFAULT 0,
    total_profit  REAL DEFAULT 0.0
);
```

### transactions
```sql
CREATE TABLE IF NOT EXISTS transactions (
    id            TEXT PRIMARY KEY,
    user_id       TEXT NOT NULL,
    stock_symbol  TEXT NOT NULL,
    company_name  TEXT NOT NULL,
    quantity      INTEGER NOT NULL,
    price         REAL NOT NULL,
    total_amount  REAL NOT NULL,
    is_buy        INTEGER NOT NULL,  -- 1=BUY, 0=SELL
    timestamp     TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### holdings
```sql
CREATE TABLE IF NOT EXISTS holdings (
    id            TEXT PRIMARY KEY,
    user_id       TEXT NOT NULL,
    symbol        TEXT NOT NULL,
    company_name  TEXT NOT NULL,
    quantity      INTEGER NOT NULL DEFAULT 0,
    avg_buy_price REAL NOT NULL DEFAULT 0.0,
    current_price REAL NOT NULL DEFAULT 0.0,
    UNIQUE(user_id, symbol),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### price_alerts
```sql
CREATE TABLE IF NOT EXISTS price_alerts (
    id            TEXT PRIMARY KEY,
    user_id       TEXT NOT NULL,
    stock_symbol  TEXT NOT NULL,
    company_name  TEXT NOT NULL,
    condition     TEXT NOT NULL,    -- 'ABOVE' or 'BELOW'
    target_price  REAL NOT NULL,
    status        TEXT DEFAULT 'ACTIVE', -- ACTIVE/TRIGGERED/DISMISSED
    created_at    TEXT NOT NULL,
    triggered_at  TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### activity_logs
```sql
CREATE TABLE IF NOT EXISTS activity_logs (
    id            TEXT PRIMARY KEY,
    user_id       TEXT NOT NULL,
    action        TEXT NOT NULL,
    description   TEXT,
    timestamp     TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Relationships Summary

| Relationship | Type | Cardinality |
|---|---|---|
| User → Transactions | One-to-Many | 1 user has many transactions |
| User → Holdings | One-to-Many | 1 user has many holdings |
| User → PriceAlerts | One-to-Many | 1 user has many alerts |
| User → ActivityLogs | One-to-Many | 1 user has many logs |
| Stock ↔ Holding | References | Many holdings reference stocks |
| Stock ↔ Transaction | References | Many transactions reference stocks |

---

## Database Configuration

| Setting | Value |
|---------|-------|
| Engine | SQLite 3.45.3 |
| File | `tradesphere.db` (project root) |
| Mode | WAL (Write-Ahead Logging) |
| Connection | Single connection per session |
| Driver | `org.xerial:sqlite-jdbc` |
