package com.tradesphere.services;

import com.tradesphere.model.Stock;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Simulates a live stock market using a background ScheduledExecutorService.
 * Price ticks occur every 2 seconds. Observers are notified on JavaFX thread.
 */
public class MarketSimulator {

    private static MarketSimulator instance;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "MarketSimulator");
        t.setDaemon(true);
        return t;
    });

    private final Map<String, Stock> stocks = new LinkedHashMap<>();
    private final List<Consumer<List<Stock>>> listeners = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    private boolean running = false;
    private int tickIntervalMs = 2000;

    // Predefined stock catalogue
    private static final Object[][] STOCK_DATA = {
        {"AAPL",  "Apple Inc.",             178.50, 2_800_000_000L, 2_800_000_000_000.0, "Technology"},
        {"TSLA",  "Tesla Inc.",             245.30, 1_200_000_000L,   780_000_000_000.0, "Automotive"},
        {"NVDA",  "NVIDIA Corporation",     875.40,   450_000_000L, 2_160_000_000_000.0, "Technology"},
        {"GOOGL", "Alphabet Inc.",          141.80, 1_800_000_000L, 1_750_000_000_000.0, "Technology"},
        {"AMZN",  "Amazon.com Inc.",        178.25, 1_600_000_000L, 1_850_000_000_000.0, "E-Commerce"},
        {"MSFT",  "Microsoft Corporation", 415.60, 2_200_000_000L, 3_080_000_000_000.0, "Technology"},
        {"META",  "Meta Platforms Inc.",    505.20,   900_000_000L, 1_290_000_000_000.0, "Social Media"},
        {"NFLX",  "Netflix Inc.",           625.80,   320_000_000L,   270_000_000_000.0, "Entertainment"},
        {"AMD",   "Advanced Micro Devices", 165.30,   800_000_000L,   267_000_000_000.0, "Technology"},
        {"INTC",  "Intel Corporation",       43.20,   950_000_000L,   183_000_000_000.0, "Technology"},
        {"BABA",  "Alibaba Group",           75.40,   600_000_000L,   203_000_000_000.0, "E-Commerce"},
        {"PYPL",  "PayPal Holdings",         65.80,   420_000_000L,    71_000_000_000.0, "Fintech"},
        {"UBER",  "Uber Technologies",       72.30,   510_000_000L,   148_000_000_000.0, "Transport"},
        {"SPOT",  "Spotify Technology",     270.50,   185_000_000L,    52_000_000_000.0, "Entertainment"},
        {"SHOP",  "Shopify Inc.",           750.20,   140_000_000L,    95_000_000_000.0, "E-Commerce"},
        {"SQ",    "Block Inc.",              75.40,   320_000_000L,    44_000_000_000.0, "Fintech"},
        {"TWTR",  "X Corp. (Twitter)",       54.20,   480_000_000L,    38_000_000_000.0, "Social Media"},
        {"COIN",  "Coinbase Global",        185.60,   210_000_000L,    44_000_000_000.0, "Fintech"},
        {"PLTR",  "Palantir Technologies",   23.80,   680_000_000L,    47_000_000_000.0, "Technology"},
        {"RIVN",  "Rivian Automotive",       16.50,   290_000_000L,    15_000_000_000.0, "Automotive"},
    };

    // ─── Singleton ────────────────────────────────────────────────────────────

    private MarketSimulator() { initStocks(); }

    public static MarketSimulator getInstance() {
        if (instance == null) instance = new MarketSimulator();
        return instance;
    }

    // ─── Init ─────────────────────────────────────────────────────────────────

    private void initStocks() {
        for (Object[] row : STOCK_DATA) {
            Stock s = new Stock(
                (String) row[0], (String) row[1],
                (double) row[2], (long) row[3],
                (double) row[4], (String) row[5]);
            stocks.put(s.getSymbol(), s);
        }
    }

    // ─── Simulation ───────────────────────────────────────────────────────────

    public void start() {
        if (running) return;
        running = true;
        scheduler.scheduleAtFixedRate(this::tick, 0, tickIntervalMs, TimeUnit.MILLISECONDS);
        System.out.println("📈 Market simulator started");
    }

    public void stop() {
        running = false;
        scheduler.shutdown();
        System.out.println("📉 Market simulator stopped");
    }

    private void tick() {
        try {
            for (Stock s : stocks.values()) {
                double current = s.getCurrentPrice();
                // Brownian motion with drift
                double volatility = getVolatility(s.getSector());
                double drift = (random.nextDouble() - 0.48) * volatility;
                double newPrice = current * (1 + drift);
                newPrice = Math.max(newPrice, 0.01);
                newPrice = Math.round(newPrice * 100.0) / 100.0;

                // Update volume
                long volChange = (long)(s.getVolume() * (random.nextDouble() * 0.02 - 0.01));
                s.volumeProperty().set(Math.max(s.getVolume() + volChange, 1_000_000));

                // Must update on JavaFX thread
                final double fp = newPrice;
                javafx.application.Platform.runLater(() -> s.updatePrice(fp));
            }
            javafx.application.Platform.runLater(() ->
                listeners.forEach(l -> l.accept(getStockList())));
        } catch (Exception e) {
            System.err.println("Market tick error: " + e.getMessage());
        }
    }

    private double getVolatility(String sector) {
        return switch (sector) {
            case "Technology"   -> 0.008;
            case "Automotive"   -> 0.012;
            case "Fintech"      -> 0.010;
            case "Social Media" -> 0.009;
            default             -> 0.007;
        };
    }

    // ─── Observer Pattern ─────────────────────────────────────────────────────

    public void addListener(Consumer<List<Stock>> listener) { listeners.add(listener); }
    public void removeListener(Consumer<List<Stock>> listener) { listeners.remove(listener); }

    // ─── Queries ──────────────────────────────────────────────────────────────

    public Stock getStock(String symbol) { return stocks.get(symbol); }
    public List<Stock> getStockList()    { return new ArrayList<>(stocks.values()); }
    public Map<String, Stock> getStockMap() { return Collections.unmodifiableMap(stocks); }

    public List<Stock> getTopGainers() {
        return stocks.values().stream()
            .sorted(Comparator.comparingDouble(Stock::getChangePct).reversed())
            .limit(5).toList();
    }

    public List<Stock> getTopLosers() {
        return stocks.values().stream()
            .sorted(Comparator.comparingDouble(Stock::getChangePct))
            .limit(5).toList();
    }

    public List<Stock> getHighVolume() {
        return stocks.values().stream()
            .sorted(Comparator.comparingLong(Stock::getVolume).reversed())
            .limit(5).toList();
    }

    public void setTickInterval(int ms) { this.tickIntervalMs = ms; }
    public boolean isRunning() { return running; }
    public int getStockCount() { return stocks.size(); }
}
