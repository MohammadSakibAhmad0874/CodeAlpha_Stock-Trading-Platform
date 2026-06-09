package com.tradesphere.model;

import javafx.beans.property.*;

/**
 * Represents a stock listed on the virtual TradeSphere market.
 * Uses JavaFX properties for live UI binding during market simulation.
 */
public class Stock {

    private final StringProperty symbol = new SimpleStringProperty();
    private final StringProperty companyName = new SimpleStringProperty();
    private final DoubleProperty currentPrice = new SimpleDoubleProperty();
    private final DoubleProperty openPrice = new SimpleDoubleProperty();
    private final DoubleProperty dayHigh = new SimpleDoubleProperty();
    private final DoubleProperty dayLow = new SimpleDoubleProperty();
    private final DoubleProperty previousClose = new SimpleDoubleProperty();
    private final DoubleProperty changeAmount = new SimpleDoubleProperty();
    private final DoubleProperty changePct = new SimpleDoubleProperty();
    private final LongProperty volume = new SimpleLongProperty();
    private final DoubleProperty marketCap = new SimpleDoubleProperty();
    private final StringProperty sector = new SimpleStringProperty();
    private final StringProperty trend = new SimpleStringProperty("NEUTRAL"); // UP, DOWN, NEUTRAL
    private final BooleanProperty inWatchlist = new SimpleBooleanProperty(false);

    // Historical prices for mini-chart (last 30 ticks)
    private double[] priceHistory = new double[30];
    private int historyIndex = 0;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Stock() {}

    public Stock(String symbol, String companyName, double price, long volume,
                 double marketCap, String sector) {
        this.symbol.set(symbol);
        this.companyName.set(companyName);
        this.currentPrice.set(price);
        this.openPrice.set(price);
        this.dayHigh.set(price);
        this.dayLow.set(price);
        this.previousClose.set(price);
        this.volume.set(volume);
        this.marketCap.set(marketCap);
        this.sector.set(sector);
        initHistory(price);
    }

    // ─── Business Logic ───────────────────────────────────────────────────────

    private void initHistory(double price) {
        for (int i = 0; i < priceHistory.length; i++) priceHistory[i] = price;
    }

    public void updatePrice(double newPrice) {
        double old = currentPrice.get();
        currentPrice.set(newPrice);

        // Day high/low
        if (newPrice > dayHigh.get()) dayHigh.set(newPrice);
        if (newPrice < dayLow.get())  dayLow.set(newPrice);

        // Change metrics
        double change = newPrice - previousClose.get();
        changeAmount.set(change);
        changePct.set(previousClose.get() != 0 ? (change / previousClose.get()) * 100.0 : 0);

        // Trend
        trend.set(newPrice > old ? "UP" : newPrice < old ? "DOWN" : "NEUTRAL");

        // History ring-buffer
        priceHistory[historyIndex % priceHistory.length] = newPrice;
        historyIndex++;
    }

    public double[] getPriceHistory() {
        // Return ordered history from oldest to newest
        double[] ordered = new double[priceHistory.length];
        for (int i = 0; i < priceHistory.length; i++) {
            ordered[i] = priceHistory[(historyIndex + i) % priceHistory.length];
        }
        return ordered;
    }

    public boolean isGaining() { return changePct.get() >= 0; }

    // ─── JavaFX Property Accessors ────────────────────────────────────────────

    public StringProperty symbolProperty() { return symbol; }
    public StringProperty companyNameProperty() { return companyName; }
    public DoubleProperty currentPriceProperty() { return currentPrice; }
    public DoubleProperty openPriceProperty() { return openPrice; }
    public DoubleProperty dayHighProperty() { return dayHigh; }
    public DoubleProperty dayLowProperty() { return dayLow; }
    public DoubleProperty previousCloseProperty() { return previousClose; }
    public DoubleProperty changeAmountProperty() { return changeAmount; }
    public DoubleProperty changePctProperty() { return changePct; }
    public LongProperty volumeProperty() { return volume; }
    public DoubleProperty marketCapProperty() { return marketCap; }
    public StringProperty sectorProperty() { return sector; }
    public StringProperty trendProperty() { return trend; }
    public BooleanProperty inWatchlistProperty() { return inWatchlist; }

    // ─── Plain Getters & Setters ──────────────────────────────────────────────

    public String getSymbol() { return symbol.get(); }
    public void setSymbol(String v) { symbol.set(v); }

    public String getCompanyName() { return companyName.get(); }
    public void setCompanyName(String v) { companyName.set(v); }

    public double getCurrentPrice() { return currentPrice.get(); }
    public void setCurrentPrice(double v) { currentPrice.set(v); }

    public double getOpenPrice() { return openPrice.get(); }
    public void setOpenPrice(double v) { openPrice.set(v); }

    public double getDayHigh() { return dayHigh.get(); }
    public void setDayHigh(double v) { dayHigh.set(v); }

    public double getDayLow() { return dayLow.get(); }
    public void setDayLow(double v) { dayLow.set(v); }

    public double getPreviousClose() { return previousClose.get(); }
    public void setPreviousClose(double v) { previousClose.set(v); }

    public double getChangeAmount() { return changeAmount.get(); }
    public double getChangePct() { return changePct.get(); }

    public long getVolume() { return volume.get(); }
    public void setVolume(long v) { volume.set(v); }

    public double getMarketCap() { return marketCap.get(); }
    public void setMarketCap(double v) { marketCap.set(v); }

    public String getSector() { return sector.get(); }
    public void setSector(String v) { sector.set(v); }

    public String getTrend() { return trend.get(); }

    public boolean isInWatchlist() { return inWatchlist.get(); }
    public void setInWatchlist(boolean v) { inWatchlist.set(v); }

    @Override
    public String toString() {
        return String.format("%s (%s) ₹%.2f [%+.2f%%]",
                companyName.get(), symbol.get(), currentPrice.get(), changePct.get());
    }
}
