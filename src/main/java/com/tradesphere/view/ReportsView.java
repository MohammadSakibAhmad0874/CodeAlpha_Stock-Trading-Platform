package com.tradesphere.view;

import com.tradesphere.controller.AppController;
import com.tradesphere.services.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.awt.Desktop;
import java.io.File;

/**
 * Reports generation: CSV / TXT / PDF export with preview.
 */
public class ReportsView {

    private final ScrollPane root;
    private final VBox content = new VBox(24);
    private final Label statusLabel = new Label();

    public ReportsView() {
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setStyle("-fx-background-color: #0d0f14;");
        buildContent();
        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#0d0f14; -fx-background:#0d0f14;");
    }

    private void buildContent() {
        VBox header = new VBox(4);
        Label title = new Label("📄 Reports & Export");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill:#e8eaed;");
        Label sub = new Label("Generate and export your trading reports in CSV, TXT, or PDF format");
        sub.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");
        header.getChildren().addAll(title, sub);

        statusLabel.setStyle("-fx-font-size:13px; -fx-padding:0;");
        statusLabel.setWrapText(true);

        HBox cards = new HBox(16);

        VBox portfolioCard = buildReportCard(
            "💼 Portfolio Report",
            "Export your current holdings, P&L, ROI and a complete breakdown of your portfolio.",
            new String[]{"CSV", "TXT", "PDF"},
            new Runnable[]{
                () -> exportAndOpen(ReportGenerator.getInstance().exportPortfolioCSV(
                    AppController.getInstance().getCurrentUser(),
                    AppController.getInstance().getPortfolio())),
                () -> exportAndOpen(ReportGenerator.getInstance().exportPortfolioTXT(
                    AppController.getInstance().getCurrentUser(),
                    AppController.getInstance().getPortfolio())),
                () -> exportAndOpen(ReportGenerator.getInstance().exportPortfolioPDF(
                    AppController.getInstance().getCurrentUser(),
                    AppController.getInstance().getPortfolio()))
            }
        );

        VBox tradesCard = buildReportCard(
            "🔄 Trade History Report",
            "Export all buy/sell transactions with timestamps, prices, quantities, and P&L.",
            new String[]{"CSV"},
            new Runnable[]{
                () -> exportAndOpen(ReportGenerator.getInstance().exportTransactionsCSV(
                    AppController.getInstance().getCurrentUser(),
                    AppController.getInstance().getTransactions()))
            }
        );

        HBox.setHgrow(portfolioCard, Priority.ALWAYS);
        HBox.setHgrow(tradesCard,   Priority.ALWAYS);
        cards.getChildren().addAll(portfolioCard, tradesCard);

        // Recent exports list
        VBox recentCard = buildRecentExports();

        content.getChildren().addAll(header, statusLabel, cards, recentCard);
    }

    private VBox buildReportCard(String title, String desc, String[] formats, Runnable[] actions) {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));

        Label t = new Label(title);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        t.setStyle("-fx-text-fill:#e8eaed;");

        Label d = new Label(desc);
        d.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
        d.setWrapText(true);

        HBox buttons = new HBox(8);
        for (int i = 0; i < formats.length; i++) {
            final int idx = i;
            String fmt = formats[i];
            String btnStyle = switch (fmt) {
                case "CSV" -> "-fx-background-color:#16a34a; -fx-text-fill:white;";
                case "TXT" -> "-fx-background-color:#2563eb; -fx-text-fill:white;";
                case "PDF" -> "-fx-background-color:#dc2626; -fx-text-fill:white;";
                default    -> "-fx-background-color:#1e2436; -fx-text-fill:#e8eaed;";
            };
            Button btn = new Button("⬇  Export " + fmt);
            btn.setStyle(btnStyle + " -fx-font-weight:bold; -fx-font-size:12px; " +
                "-fx-background-radius:8; -fx-padding:8 18; -fx-cursor:hand;");
            btn.setOnAction(e -> actions[idx].run());
            buttons.getChildren().add(btn);
        }

        card.getChildren().addAll(t, d, buttons);
        return card;
    }

    private VBox buildRecentExports() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label t = new Label("📁 Reports Folder");
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        t.setStyle("-fx-text-fill:#e8eaed;");

        File dir = new File("reports");
        VBox list = new VBox(6);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    HBox row = new HBox(12);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(6, 8, 6, 8));
                    row.setStyle("-fx-background-color:rgba(255,255,255,0.03); -fx-background-radius:6;");
                    String icon = f.getName().endsWith(".pdf") ? "📕" :
                                  f.getName().endsWith(".csv") ? "📗" : "📄";
                    Label ico = new Label(icon); ico.setStyle("-fx-font-size:14px;");
                    Label name = new Label(f.getName());
                    name.setStyle("-fx-text-fill:#e8eaed; -fx-font-size:12px;");
                    Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                    Label size = new Label(String.format("%.1f KB", f.length() / 1024.0));
                    size.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px;");
                    Button open = new Button("Open");
                    open.setStyle("-fx-background-color:#1e2436; -fx-text-fill:#3b82f6; " +
                        "-fx-background-radius:5; -fx-padding:3 10; -fx-cursor:hand; -fx-font-size:11px;");
                    open.setOnAction(e -> openFile(f));
                    row.getChildren().addAll(ico, name, sp, size, open);
                    list.getChildren().add(row);
                }
            } else {
                list.getChildren().add(noFilesLabel());
            }
        } else {
            list.getChildren().add(noFilesLabel());
        }

        Button openFolder = new Button("📂  Open Reports Folder");
        openFolder.getStyleClass().add("btn-outline");
        openFolder.setOnAction(e -> {
            try { Desktop.getDesktop().open(new File("reports")); }
            catch (Exception ex) { statusLabel.setText("Cannot open folder: " + ex.getMessage()); }
        });

        card.getChildren().addAll(t, list, openFolder);
        return card;
    }

    private Label noFilesLabel() {
        Label l = new Label("No reports generated yet. Use the buttons above to create your first report.");
        l.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px; -fx-padding:8 0;");
        return l;
    }

    private void exportAndOpen(String filename) {
        if (filename == null) {
            statusLabel.setText("❌ Export failed. Check console for details.");
            statusLabel.setStyle("-fx-text-fill:#ef4444; -fx-font-size:13px;");
            return;
        }
        statusLabel.setText("✅ Exported: " + filename);
        statusLabel.setStyle("-fx-text-fill:#22c55e; -fx-font-size:13px;");
        AppController.getInstance().logActivity(
            com.tradesphere.model.ActivityLog.Action.REPORT_EXPORTED, "Exported: " + filename);
        NotificationManager.getInstance().success("Report Exported", "Saved to: " + filename);
        openFile(new File(filename));
    }

    private void openFile(File f) {
        try { if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(f); }
        catch (Exception e) { System.err.println("Cannot open file: " + e.getMessage()); }
    }

    public Parent getRoot() { return root; }
}
