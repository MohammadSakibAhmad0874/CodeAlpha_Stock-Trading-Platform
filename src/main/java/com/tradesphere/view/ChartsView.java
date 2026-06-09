package com.tradesphere.view;

import com.tradesphere.controller.AppController;
import com.tradesphere.model.*;
import com.tradesphere.services.*;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.util.*;

/**
 * Charts: Portfolio Growth (line), Sector Allocation (pie), P&L Bar chart — all drawn on Canvas.
 */
public class ChartsView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);

    public ChartsView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");
        buildContent();
        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #0d0f14; -fx-background: #0d0f14;");
    }

    private void buildContent() {
        Label title = new Label("📉 Charts & Analytics");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #e8eaed;");
        Label sub = new Label("Visual performance analysis of your portfolio and market");
        sub.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");

        HBox row1 = new HBox(16);
        VBox portfolioChart = buildPortfolioGrowthChart();
        VBox sectorChart    = buildSectorPieChart();
        HBox.setHgrow(portfolioChart, Priority.ALWAYS);
        row1.getChildren().addAll(portfolioChart, sectorChart);

        HBox row2 = new HBox(16);
        VBox plChart     = buildPLBarChart();
        VBox priceChart  = buildStockPriceChart();
        HBox.setHgrow(plChart, Priority.ALWAYS);
        HBox.setHgrow(priceChart, Priority.ALWAYS);
        row2.getChildren().addAll(plChart, priceChart);

        content.getChildren().addAll(new VBox(4, title, sub), row1, row2);
    }

    // ─── Portfolio Growth Line Chart ──────────────────────────────────────────

    private VBox buildPortfolioGrowthChart() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label hdr = new Label("💼 Portfolio Value Growth");
        hdr.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        hdr.setStyle("-fx-text-fill:#e8eaed;");

        Canvas canvas = new Canvas(520, 260);
        drawLineChart(canvas);

        // Animate line drawing on every market tick
        MarketSimulator.getInstance().addListener(s -> drawLineChart(canvas));

        card.getChildren().addAll(hdr, canvas);
        return card;
    }

    private void drawLineChart(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        double pad = 40;

        // Background
        gc.setFill(Color.web("#141820")); gc.fillRoundRect(0,0,w,h,12,12);

        // Get price history from any held stock (simulate portfolio history)
        Portfolio p = AppController.getInstance().getPortfolio();
        double[] history;
        if (p != null && !p.getHoldings().isEmpty()) {
            String sym = p.getHoldings().keySet().iterator().next();
            Stock s = MarketSimulator.getInstance().getStock(sym);
            history = s != null ? s.getPriceHistory() : generateFakeHistory(100000, 30);
        } else {
            history = generateFakeHistory(100000, 30);
        }

        double min = Arrays.stream(history).min().getAsDouble() * 0.98;
        double max = Arrays.stream(history).max().getAsDouble() * 1.02;
        double range = max - min;
        int n = history.length;

        // Grid lines
        gc.setStroke(Color.web("#1e2436")); gc.setLineWidth(1);
        for (int i = 0; i <= 4; i++) {
            double y = pad + (h - 2*pad) * i / 4;
            gc.strokeLine(pad, y, w-pad, y);
            double val = max - range * i / 4;
            gc.setFill(Color.web("#334155"));
            gc.setFont(Font.font("Segoe UI", 10));
            gc.fillText(String.format("₹%.0f", val), 2, y+4);
        }

        // Gradient fill under line
        LinearGradient fillGrad = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(59,130,246,0.3)), new Stop(1, Color.rgb(59,130,246,0)));
        gc.setFill(fillGrad);
        gc.beginPath();
        for (int i = 0; i < n; i++) {
            double x = pad + (w-2*pad) * i / (n-1);
            double y = pad + (h-2*pad) * (1 - (history[i]-min)/range);
            if (i==0) gc.moveTo(x,y); else gc.lineTo(x,y);
        }
        gc.lineTo(w-pad, h-pad); gc.lineTo(pad, h-pad); gc.closePath(); gc.fill();

        // Line
        gc.setStroke(Color.web("#3b82f6")); gc.setLineWidth(2.5);
        gc.beginPath();
        for (int i = 0; i < n; i++) {
            double x = pad + (w-2*pad) * i / (n-1);
            double y = pad + (h-2*pad) * (1 - (history[i]-min)/range);
            if (i==0) gc.moveTo(x,y); else gc.lineTo(x,y);
        }
        gc.stroke();

        // Dots
        gc.setFill(Color.web("#3b82f6"));
        for (int i = 0; i < n; i += 5) {
            double x = pad + (w-2*pad) * i / (n-1);
            double y = pad + (h-2*pad) * (1 - (history[i]-min)/range);
            gc.fillOval(x-4, y-4, 8, 8);
        }
        // Last dot highlighted
        double lx = pad + (w-2*pad)*(n-1)/(n-1);
        double ly = pad + (h-2*pad)*(1-(history[n-1]-min)/range);
        gc.setFill(Color.WHITE); gc.fillOval(lx-5, ly-5, 10, 10);
        gc.setFill(Color.web("#3b82f6")); gc.fillOval(lx-3, ly-3, 6, 6);
    }

    // ─── Sector Pie Chart ─────────────────────────────────────────────────────

    private VBox buildSectorPieChart() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setPrefWidth(300);

        Label hdr = new Label("🥧 Sector Allocation");
        hdr.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        hdr.setStyle("-fx-text-fill:#e8eaed;");

        Canvas canvas = new Canvas(260, 260);
        drawPieChart(canvas);

        card.getChildren().addAll(hdr, canvas);
        return card;
    }

    private void drawPieChart(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        gc.setFill(Color.web("#141820")); gc.fillRoundRect(0,0,w,h,12,12);

        // Sector data from portfolio or default
        Map<String, Double> sectors = new LinkedHashMap<>();
        Portfolio p = AppController.getInstance().getPortfolio();
        if (p != null && !p.getHoldings().isEmpty()) {
            for (Portfolio.Holding hld : p.getHoldings().values()) {
                Stock s = MarketSimulator.getInstance().getStock(hld.getSymbol());
                if (s != null) sectors.merge(s.getSector(), hld.getInvestedAmount(), Double::sum);
            }
        }
        if (sectors.isEmpty()) {
            sectors.put("Technology", 45.0); sectors.put("Automotive", 20.0);
            sectors.put("E-Commerce", 15.0); sectors.put("Fintech", 12.0); sectors.put("Other", 8.0);
        }

        String[] colors = {"#3b82f6","#22c55e","#f59e0b","#ef4444","#8b5cf6","#06b6d4","#ec4899"};
        double total = sectors.values().stream().mapToDouble(Double::doubleValue).sum();
        double startAngle = -90;
        double cx = w/2, cy = h/2 - 10, r = 90;

        int i = 0;
        for (Map.Entry<String,Double> e : sectors.entrySet()) {
            double extent = 360 * e.getValue() / total;
            gc.setFill(Color.web(colors[i % colors.length]));
            gc.fillArc(cx-r, cy-r, 2*r, 2*r, startAngle, extent, javafx.scene.shape.ArcType.ROUND);
            startAngle += extent; i++;
        }
        // Inner circle (donut)
        gc.setFill(Color.web("#141820")); gc.fillOval(cx-50, cy-50, 100, 100);

        // Legend
        double lx = 10, ly = h - sectors.size()*18 - 4;
        i = 0;
        for (Map.Entry<String,Double> e : sectors.entrySet()) {
            gc.setFill(Color.web(colors[i%colors.length]));
            gc.fillRoundRect(lx, ly, 10, 10, 3, 3);
            gc.setFill(Color.web("#94a3b8"));
            gc.setFont(Font.font("Segoe UI", 10));
            gc.fillText(e.getKey() + " " + String.format("%.0f%%", 100*e.getValue()/total), lx+14, ly+9);
            ly += 18; i++;
        }
    }

    // ─── P&L Bar Chart ────────────────────────────────────────────────────────

    private VBox buildPLBarChart() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label hdr = new Label("📊 Stock P&L Breakdown");
        hdr.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        hdr.setStyle("-fx-text-fill:#e8eaed;");

        Canvas canvas = new Canvas(400, 260);
        drawBarChart(canvas);
        MarketSimulator.getInstance().addListener(s -> drawBarChart(canvas));

        card.getChildren().addAll(hdr, canvas);
        return card;
    }

    private void drawBarChart(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        gc.setFill(Color.web("#141820")); gc.fillRoundRect(0,0,w,h,12,12);

        Portfolio p = AppController.getInstance().getPortfolio();
        if (p == null || p.getHoldings().isEmpty()) {
            gc.setFill(Color.web("#334155"));
            gc.setFont(Font.font("Segoe UI", 13));
            gc.fillText("No holdings to display", w/2-80, h/2);
            return;
        }

        List<Portfolio.Holding> holdings = new ArrayList<>(p.getHoldings().values());
        int n = Math.min(holdings.size(), 8);
        double pad = 40, barW = (w - 2*pad) / (n * 1.5);
        double maxAbs = holdings.stream().mapToDouble(hld -> Math.abs(hld.getProfitOrLoss())).max().orElse(1);
        double midY = h/2;

        gc.setStroke(Color.web("#252d42")); gc.setLineWidth(1);
        gc.strokeLine(pad, midY, w-pad, midY);

        for (int i = 0; i < n; i++) {
            Portfolio.Holding hld = holdings.get(i);
            double pl = hld.getProfitOrLoss();
            double barH = (Math.abs(pl) / maxAbs) * (h/2 - pad - 10);
            double x = pad + i * (barW * 1.5);
            boolean pos = pl >= 0;

            LinearGradient barGrad = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE,
                pos ? new Stop[]{new Stop(0,Color.web("#22c55e")),new Stop(1,Color.web("#16a34a"))}
                    : new Stop[]{new Stop(0,Color.web("#ef4444")),new Stop(1,Color.web("#dc2626"))});
            gc.setFill(barGrad);
            double barY = pos ? midY - barH : midY;
            gc.fillRoundRect(x, barY, barW, barH, 4, 4);

            gc.setFill(Color.web("#94a3b8"));
            gc.setFont(Font.font("Segoe UI", 9));
            gc.fillText(hld.getSymbol(), x, h-4);
            gc.setFill(pos ? Color.web("#22c55e") : Color.web("#ef4444"));
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
            gc.fillText(String.format("%+.0f", pl), x, pos ? barY-4 : barY+barH+11);
        }
    }

    // ─── Stock Price Line Chart ────────────────────────────────────────────────

    private VBox buildStockPriceChart() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label hdr = new Label("📈 Stock Price History (AAPL)");
        hdr.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        hdr.setStyle("-fx-text-fill:#e8eaed;");

        ComboBox<String> picker = new ComboBox<>();
        picker.getStyleClass().add("combo-box");
        picker.setPrefWidth(200);
        MarketSimulator.getInstance().getStockList()
            .forEach(s -> picker.getItems().add(s.getSymbol() + " – " + s.getCompanyName()));
        picker.setValue("AAPL – Apple Inc.");

        Canvas canvas = new Canvas(380, 220);

        Runnable redraw = () -> {
            String sel = picker.getValue();
            if (sel == null) return;
            String sym = sel.split(" – ")[0];
            Stock s = MarketSimulator.getInstance().getStock(sym);
            if (s != null) drawMiniLine(canvas, s.getPriceHistory(),
                s.getChangePct() >= 0 ? "#22c55e" : "#ef4444");
        };
        picker.setOnAction(e -> redraw.run());
        MarketSimulator.getInstance().addListener(s -> redraw.run());
        redraw.run();

        card.getChildren().addAll(hdr, picker, canvas);
        return card;
    }

    private void drawMiniLine(Canvas canvas, double[] history, String lineColor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        double pad = 30;
        gc.setFill(Color.web("#141820")); gc.fillRoundRect(0,0,w,h,12,12);

        double min = Arrays.stream(history).min().getAsDouble() * 0.99;
        double max = Arrays.stream(history).max().getAsDouble() * 1.01;
        double range = max - min;
        int n = history.length;

        gc.setStroke(Color.web(lineColor)); gc.setLineWidth(2);
        gc.beginPath();
        for (int i = 0; i < n; i++) {
            double x = pad + (w-2*pad)*i/(n-1);
            double y = pad + (h-2*pad)*(1-(history[i]-min)/range);
            if (i==0) gc.moveTo(x,y); else gc.lineTo(x,y);
        }
        gc.stroke();

        // Labels
        gc.setFill(Color.web("#94a3b8")); gc.setFont(Font.font("Segoe UI", 10));
        gc.fillText(String.format("₹%.2f", max), 2, pad);
        gc.fillText(String.format("₹%.2f", min), 2, h-pad+10);
        gc.fillText(String.format("₹%.2f", history[n-1]), w-70, pad);
    }

    private double[] generateFakeHistory(double base, int n) {
        double[] h = new double[n];
        Random rnd = new Random(42);
        h[0] = base;
        for (int i = 1; i < n; i++) h[i] = h[i-1] * (1 + (rnd.nextDouble()-0.48)*0.008);
        return h;
    }

    public Parent getRoot() { return root; }
}
