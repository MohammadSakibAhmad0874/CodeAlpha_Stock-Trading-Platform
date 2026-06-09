package com.tradesphere.database;

import com.tradesphere.model.*;
import com.tradesphere.model.Transaction.Type;

import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * Singleton SQLite database manager.
 * Handles all CRUD operations for the TradeSphere Pro data layer.
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    private static final String DB_PATH = "data/tradesphere.db";

    // ─── Singleton ────────────────────────────────────────────────────────────

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    // ─── Initialisation ───────────────────────────────────────────────────────

    public void init() {
        try {
            new File("data").mkdirs();
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            connection.createStatement().execute("PRAGMA journal_mode=WAL");
            createTables();
            System.out.println("✅ Database initialised at " + DB_PATH);
        } catch (Exception e) {
            System.err.println("❌ Database init failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String[] ddl = {
            // Users
            """
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                full_name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                wallet_balance REAL DEFAULT 100000,
                initial_balance REAL DEFAULT 100000,
                theme TEXT DEFAULT 'DARK',
                currency TEXT DEFAULT 'INR',
                notifications_enabled INTEGER DEFAULT 1,
                remember_me INTEGER DEFAULT 0,
                created_at TEXT,
                last_login TEXT,
                total_trades INTEGER DEFAULT 0,
                total_profit REAL DEFAULT 0
            )
            """,
            // Holdings
            """
            CREATE TABLE IF NOT EXISTS holdings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL,
                symbol TEXT NOT NULL,
                company_name TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                avg_buy_price REAL NOT NULL,
                UNIQUE(user_id, symbol),
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """,
            // Transactions
            """
            CREATE TABLE IF NOT EXISTS transactions (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                stock_symbol TEXT NOT NULL,
                company_name TEXT NOT NULL,
                type TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                price_per_share REAL NOT NULL,
                total_amount REAL NOT NULL,
                profit_or_loss REAL DEFAULT 0,
                timestamp TEXT NOT NULL,
                notes TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """,
            // Price Alerts
            """
            CREATE TABLE IF NOT EXISTS price_alerts (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                stock_symbol TEXT NOT NULL,
                company_name TEXT NOT NULL,
                condition TEXT NOT NULL,
                target_price REAL NOT NULL,
                status TEXT DEFAULT 'ACTIVE',
                created_at TEXT NOT NULL,
                triggered_at TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """,
            // Activity Logs
            """
            CREATE TABLE IF NOT EXISTS activity_logs (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                action TEXT NOT NULL,
                description TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                metadata TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """,
            // Watchlist
            """
            CREATE TABLE IF NOT EXISTS watchlist (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL,
                symbol TEXT NOT NULL,
                UNIQUE(user_id, symbol),
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """
        };

        try (Statement st = connection.createStatement()) {
            for (String sql : ddl) st.execute(sql);
        }
    }

    // ─── USER CRUD ────────────────────────────────────────────────────────────

    public boolean saveUser(User u) {
        String sql = """
            INSERT OR REPLACE INTO users
            (id,full_name,email,username,password_hash,wallet_balance,initial_balance,
             theme,currency,notifications_enabled,remember_me,created_at,last_login,
             total_trades,total_profit)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getId());
            ps.setString(2, u.getFullName());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getUsername());
            ps.setString(5, u.getPasswordHash());
            ps.setDouble(6, u.getWalletBalance());
            ps.setDouble(7, u.getInitialBalance());
            ps.setString(8, u.getTheme());
            ps.setString(9, u.getCurrency());
            ps.setInt(10, u.isNotificationsEnabled() ? 1 : 0);
            ps.setInt(11, u.isRememberMe() ? 1 : 0);
            ps.setString(12, u.getCreatedAt());
            ps.setString(13, u.getLastLogin());
            ps.setInt(14, u.getTotalTrades());
            ps.setDouble(15, u.getTotalProfit());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("saveUser error: " + e.getMessage());
            return false;
        }
    }

    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return null;
    }

    public User findUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY wallet_balance DESC")) {
            while (rs.next()) users.add(mapUser(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return users;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getString("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setWalletBalance(rs.getDouble("wallet_balance"));
        u.setInitialBalance(rs.getDouble("initial_balance"));
        u.setTheme(rs.getString("theme"));
        u.setCurrency(rs.getString("currency"));
        u.setNotificationsEnabled(rs.getInt("notifications_enabled") == 1);
        u.setRememberMe(rs.getInt("remember_me") == 1);
        u.setCreatedAt(rs.getString("created_at"));
        u.setLastLogin(rs.getString("last_login"));
        u.setTotalTrades(rs.getInt("total_trades"));
        u.setTotalProfit(rs.getDouble("total_profit"));
        return u;
    }

    // ─── HOLDINGS CRUD ────────────────────────────────────────────────────────

    public void saveHolding(String userId, Portfolio.Holding h) {
        String sql = """
            INSERT INTO holdings (user_id,symbol,company_name,quantity,avg_buy_price)
            VALUES (?,?,?,?,?)
            ON CONFLICT(user_id,symbol) DO UPDATE SET quantity=excluded.quantity,
            avg_buy_price=excluded.avg_buy_price
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, h.getSymbol());
            ps.setString(3, h.getCompanyName());
            ps.setInt(4, h.getQuantity());
            ps.setDouble(5, h.getAvgBuyPrice());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("saveHolding: " + e.getMessage()); }
    }

    public void deleteHolding(String userId, String symbol) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM holdings WHERE user_id=? AND symbol=?")) {
            ps.setString(1, userId); ps.setString(2, symbol);
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("deleteHolding: " + e.getMessage()); }
    }

    public Map<String, Portfolio.Holding> loadHoldings(String userId) {
        Map<String, Portfolio.Holding> map = new HashMap<>();
        String sql = "SELECT * FROM holdings WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Portfolio.Holding h = new Portfolio.Holding(
                    rs.getString("symbol"), rs.getString("company_name"),
                    rs.getInt("quantity"), rs.getDouble("avg_buy_price"));
                map.put(h.getSymbol(), h);
            }
        } catch (SQLException e) { System.err.println("loadHoldings: " + e.getMessage()); }
        return map;
    }

    // ─── TRANSACTION CRUD ─────────────────────────────────────────────────────

    public void saveTransaction(Transaction t) {
        String sql = """
            INSERT OR IGNORE INTO transactions
            (id,user_id,stock_symbol,company_name,type,quantity,price_per_share,
             total_amount,profit_or_loss,timestamp,notes)
            VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, t.getId());
            ps.setString(2, t.getUserId());
            ps.setString(3, t.getStockSymbol());
            ps.setString(4, t.getCompanyName());
            ps.setString(5, t.getType().name());
            ps.setInt(6, t.getQuantity());
            ps.setDouble(7, t.getPricePerShare());
            ps.setDouble(8, t.getTotalAmount());
            ps.setDouble(9, t.getProfitOrLoss());
            ps.setString(10, t.getTimestamp());
            ps.setString(11, t.getNotes());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("saveTransaction: " + e.getMessage()); }
    }

    public List<Transaction> loadTransactions(String userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id=? ORDER BY timestamp DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getString("id"));
                t.setUserId(rs.getString("user_id"));
                t.setStockSymbol(rs.getString("stock_symbol"));
                t.setCompanyName(rs.getString("company_name"));
                t.setType(Type.valueOf(rs.getString("type")));
                t.setQuantity(rs.getInt("quantity"));
                t.setPricePerShare(rs.getDouble("price_per_share"));
                t.setTotalAmount(rs.getDouble("total_amount"));
                t.setProfitOrLoss(rs.getDouble("profit_or_loss"));
                t.setTimestamp(rs.getString("timestamp"));
                t.setNotes(rs.getString("notes"));
                list.add(t);
            }
        } catch (SQLException e) { System.err.println("loadTransactions: " + e.getMessage()); }
        return list;
    }

    // ─── PRICE ALERTS CRUD ────────────────────────────────────────────────────

    public void saveAlert(PriceAlert a) {
        String sql = """
            INSERT OR REPLACE INTO price_alerts
            (id,user_id,stock_symbol,company_name,condition,target_price,status,created_at,triggered_at)
            VALUES (?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, a.getId());
            ps.setString(2, a.getUserId());
            ps.setString(3, a.getStockSymbol());
            ps.setString(4, a.getCompanyName());
            ps.setString(5, a.getCondition().name());
            ps.setDouble(6, a.getTargetPrice());
            ps.setString(7, a.getStatus().name());
            ps.setString(8, a.getCreatedAt());
            ps.setString(9, a.getTriggeredAt());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("saveAlert: " + e.getMessage()); }
    }

    public List<PriceAlert> loadAlerts(String userId) {
        List<PriceAlert> list = new ArrayList<>();
        String sql = "SELECT * FROM price_alerts WHERE user_id=? ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PriceAlert a = new PriceAlert();
                a.setId(rs.getString("id"));
                a.setUserId(rs.getString("user_id"));
                a.setStockSymbol(rs.getString("stock_symbol"));
                a.setCompanyName(rs.getString("company_name"));
                a.setCondition(PriceAlert.Condition.valueOf(rs.getString("condition")));
                a.setTargetPrice(rs.getDouble("target_price"));
                a.setStatus(PriceAlert.Status.valueOf(rs.getString("status")));
                a.setCreatedAt(rs.getString("created_at"));
                a.setTriggeredAt(rs.getString("triggered_at"));
                list.add(a);
            }
        } catch (SQLException e) { System.err.println("loadAlerts: " + e.getMessage()); }
        return list;
    }

    public void deleteAlert(String alertId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM price_alerts WHERE id=?")) {
            ps.setString(1, alertId); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("deleteAlert: " + e.getMessage()); }
    }

    // ─── ACTIVITY LOG ─────────────────────────────────────────────────────────

    public void saveLog(ActivityLog log) {
        String sql = """
            INSERT OR IGNORE INTO activity_logs (id,user_id,action,description,timestamp,metadata)
            VALUES (?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, log.getId());
            ps.setString(2, log.getUserId());
            ps.setString(3, log.getAction().name());
            ps.setString(4, log.getDescription());
            ps.setString(5, log.getTimestamp());
            ps.setString(6, log.getMetadata());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("saveLog: " + e.getMessage()); }
    }

    public List<ActivityLog> loadLogs(String userId) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE user_id=? ORDER BY timestamp DESC LIMIT 200";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setId(rs.getString("id"));
                log.setUserId(rs.getString("user_id"));
                log.setAction(ActivityLog.Action.valueOf(rs.getString("action")));
                log.setDescription(rs.getString("description"));
                log.setTimestamp(rs.getString("timestamp"));
                log.setMetadata(rs.getString("metadata"));
                list.add(log);
            }
        } catch (SQLException e) { System.err.println("loadLogs: " + e.getMessage()); }
        return list;
    }

    // ─── WATCHLIST ────────────────────────────────────────────────────────────

    public void addToWatchlist(String userId, String symbol) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR IGNORE INTO watchlist (user_id,symbol) VALUES (?,?)")) {
            ps.setString(1, userId); ps.setString(2, symbol); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("addWatchlist: " + e.getMessage()); }
    }

    public void removeFromWatchlist(String userId, String symbol) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM watchlist WHERE user_id=? AND symbol=?")) {
            ps.setString(1, userId); ps.setString(2, symbol); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("removeWatchlist: " + e.getMessage()); }
    }

    public Set<String> loadWatchlist(String userId) {
        Set<String> set = new HashSet<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT symbol FROM watchlist WHERE user_id=?")) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) set.add(rs.getString("symbol"));
        } catch (SQLException e) { System.err.println("loadWatchlist: " + e.getMessage()); }
        return set;
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    public void close() {
        try { if (connection != null) connection.close(); }
        catch (SQLException e) { System.err.println("DB close error: " + e.getMessage()); }
    }

    public Connection getConnection() { return connection; }
}
