package com.tradesphere.services;

import com.tradesphere.model.Stock;

import java.util.List;

/**
 * Generates AI-style BUY / HOLD / SELL recommendations using
 * technical indicators derived from simulated price data.
 */
public class AIInsightsService {

    public enum Signal { BUY, HOLD, SELL }

    public record Recommendation(
        String symbol,
        String companyName,
        Signal signal,
        int confidence,       // 0–100
        String rationale,
        String sector
    ) {}

    private static AIInsightsService instance;

    private AIInsightsService() {}

    public static AIInsightsService getInstance() {
        if (instance == null) instance = new AIInsightsService();
        return instance;
    }

    /**
     * Generate recommendations for all stocks.
     */
    public List<Recommendation> generateAll(List<Stock> stocks) {
        return stocks.stream().map(this::analyze).toList();
    }

    /**
     * Analyze a single stock using rule-based scoring.
     */
    public Recommendation analyze(Stock stock) {
        double[] history = stock.getPriceHistory();
        double current   = stock.getCurrentPrice();
        double changePct = stock.getChangePct();
        double dayLow    = stock.getDayLow();
        double dayHigh   = stock.getDayHigh();

        // Simple Moving Average (5-tick vs 20-tick)
        double sma5  = average(history, history.length - 5, 5);
        double sma20 = average(history, 0, 20);

        // RSI-like momentum (simplified)
        int gains = 0, losses = 0;
        for (int i = 1; i < history.length; i++) {
            if (history[i] > history[i-1]) gains++;
            else losses++;
        }
        double momentum = gains == 0 ? 0 : (double) gains / (gains + losses);

        // Price position in day range
        double range    = dayHigh - dayLow;
        double position = range > 0 ? (current - dayLow) / range : 0.5;

        // Score calculation
        int score = 50; // neutral baseline
        if (sma5 > sma20)   score += 15;  // golden cross
        else                 score -= 15;  // death cross
        if (changePct > 2)   score += 10;
        else if (changePct < -2) score -= 10;
        if (momentum > 0.6)  score += 12;
        else if (momentum < 0.4) score -= 12;
        if (position < 0.3)  score += 8;  // near day low → oversold
        if (position > 0.8)  score -= 8;  // near day high → overbought
        score = Math.max(0, Math.min(100, score));

        Signal signal;
        String rationale;
        int confidence;

        if (score >= 65) {
            signal = Signal.BUY;
            confidence = score;
            rationale = buildRationale(signal, sma5, sma20, changePct, momentum);
        } else if (score <= 35) {
            signal = Signal.SELL;
            confidence = 100 - score;
            rationale = buildRationale(signal, sma5, sma20, changePct, momentum);
        } else {
            signal = Signal.HOLD;
            confidence = 50 + Math.abs(score - 50);
            rationale = "Price is consolidating. No strong signal detected. Monitor closely.";
        }

        return new Recommendation(
            stock.getSymbol(), stock.getCompanyName(),
            signal, confidence, rationale, stock.getSector()
        );
    }

    private double average(double[] arr, int from, int count) {
        if (arr == null || arr.length == 0) return 0;
        from  = Math.max(0, from);
        count = Math.min(count, arr.length - from);
        if (count <= 0) return 0;
        double sum = 0;
        for (int i = from; i < from + count; i++) sum += arr[i];
        return sum / count;
    }

    private String buildRationale(Signal signal, double sma5, double sma20,
                                   double changePct, double momentum) {
        String cross = sma5 > sma20 ? "bullish crossover (SMA5 > SMA20)" : "bearish crossover (SMA5 < SMA20)";
        String mom   = momentum > 0.55 ? "strong upward momentum" : "weakening momentum";
        String chg   = String.format("%.1f%%", changePct);
        return switch (signal) {
            case BUY  -> String.format("Detected %s with %s. Day change: %s. Good entry opportunity.", cross, mom, chg);
            case SELL -> String.format("Detected %s with %s. Day change: %s. Consider taking profits.", cross, mom, chg);
            default   -> "Market conditions are neutral.";
        };
    }

    public Signal getSignalColor(Signal s) { return s; }
}
