package com.tradesphere.services;

import com.tradesphere.model.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generates trading reports in CSV, TXT, and PDF formats.
 */
public class ReportGenerator {

    private static ReportGenerator instance;
    private static final String REPORTS_DIR = "reports";

    private ReportGenerator() { new File(REPORTS_DIR).mkdirs(); }

    public static ReportGenerator getInstance() {
        if (instance == null) instance = new ReportGenerator();
        return instance;
    }

    private String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    // ─── CSV Export ───────────────────────────────────────────────────────────

    public String exportTransactionsCSV(User user, List<Transaction> transactions) {
        String filename = REPORTS_DIR + "/TradeSphere_Trades_" + timestamp() + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("TradeSphere Pro – Trading Report");
            pw.println("User: " + user.getFullName() + " (@" + user.getUsername() + ")");
            pw.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pw.println();
            pw.println("ID,Symbol,Company,Type,Quantity,Price/Share,Total Amount,P&L,Timestamp");
            for (Transaction t : transactions) {
                pw.printf("%s,%s,%s,%s,%d,%.2f,%.2f,%.2f,%s%n",
                    t.getId(), t.getStockSymbol(), t.getCompanyName(),
                    t.getTypeLabel(), t.getQuantity(), t.getPricePerShare(),
                    t.getTotalAmount(), t.getProfitOrLoss(), t.getTimestamp());
            }
        } catch (IOException e) { System.err.println("CSV export error: " + e.getMessage()); return null; }
        return filename;
    }

    public String exportPortfolioCSV(User user, Portfolio portfolio) {
        String filename = REPORTS_DIR + "/TradeSphere_Portfolio_" + timestamp() + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("TradeSphere Pro – Portfolio Report");
            pw.println("User: " + user.getFullName() + " (@" + user.getUsername() + ")");
            pw.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pw.println("Cash Balance: ₹" + String.format("%.2f", user.getWalletBalance()));
            pw.println();
            pw.println("Symbol,Company,Qty,Avg Buy Price,Current Price,Invested,Current Value,P&L,Return%");
            for (Portfolio.Holding h : portfolio.getHoldings().values()) {
                pw.printf("%s,%s,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%%%n",
                    h.getSymbol(), h.getCompanyName(), h.getQuantity(),
                    h.getAvgBuyPrice(), h.getCurrentPrice(),
                    h.getInvestedAmount(), h.getCurrentValue(),
                    h.getProfitOrLoss(), h.getReturnPct());
            }
            pw.println();
            pw.printf("TOTAL,,,,,,%.2f,%.2f,%.2f%%%n",
                portfolio.getTotalCurrentValue(),
                portfolio.getTotalProfitOrLoss(),
                portfolio.getOverallReturnPct());
        } catch (IOException e) { System.err.println("Portfolio CSV error: " + e.getMessage()); return null; }
        return filename;
    }

    // ─── TXT Export ───────────────────────────────────────────────────────────

    public String exportPortfolioTXT(User user, Portfolio portfolio) {
        String filename = REPORTS_DIR + "/TradeSphere_Portfolio_" + timestamp() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            String line = "═".repeat(70);
            pw.println(line);
            pw.println("                  TRADESPHERE PRO – PORTFOLIO REPORT");
            pw.println(line);
            pw.printf("User       : %s (@%s)%n", user.getFullName(), user.getUsername());
            pw.printf("Generated  : %s%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pw.printf("Cash Balance: ₹%.2f%n", user.getWalletBalance());
            pw.println(line);
            pw.printf("%-6s %-20s %5s %10s %10s %12s %10s%n",
                "SYM", "COMPANY", "QTY", "AVG PRICE", "CUR PRICE", "VALUE", "P&L");
            pw.println("─".repeat(70));
            for (Portfolio.Holding h : portfolio.getHoldings().values()) {
                pw.printf("%-6s %-20s %5d %10.2f %10.2f %12.2f %+10.2f%n",
                    h.getSymbol(), truncate(h.getCompanyName(), 20),
                    h.getQuantity(), h.getAvgBuyPrice(), h.getCurrentPrice(),
                    h.getCurrentValue(), h.getProfitOrLoss());
            }
            pw.println(line);
            pw.printf("Total Invested    : ₹%.2f%n", portfolio.getTotalInvested());
            pw.printf("Total Value       : ₹%.2f%n", portfolio.getTotalCurrentValue());
            pw.printf("Net P&L           : ₹%+.2f%n", portfolio.getTotalProfitOrLoss());
            pw.printf("Overall Return    : %.2f%%%n",  portfolio.getOverallReturnPct());
            pw.println(line);
            pw.println("TradeSphere Pro  |  Learn, Trade, Analyze.  |  v1.0");
        } catch (IOException e) { System.err.println("TXT export error: " + e.getMessage()); return null; }
        return filename;
    }

    // ─── PDF Export ───────────────────────────────────────────────────────────

    public String exportPortfolioPDF(User user, Portfolio portfolio) {
        String filename = REPORTS_DIR + "/TradeSphere_Portfolio_" + timestamp() + ".pdf";
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType1Font bold   = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float pageWidth = page.getMediaBox().getWidth();
                float margin = 50;
                float y = 780;

                // Header background
                cs.setNonStrokingColor(new PDColor(new float[]{0.07f, 0.08f, 0.13f}, PDDeviceRGB.INSTANCE));
                cs.addRect(0, 750, pageWidth, 50);
                cs.fill();

                // Title
                cs.setNonStrokingColor(new PDColor(new float[]{1f, 1f, 1f}, PDDeviceRGB.INSTANCE));
                cs.beginText();
                cs.setFont(bold, 20);
                cs.newLineAtOffset(margin, 762);
                cs.showText("TradeSphere Pro – Portfolio Report");
                cs.endText();

                y = 730;
                cs.setNonStrokingColor(new PDColor(new float[]{0f, 0f, 0f}, PDDeviceRGB.INSTANCE));

                // User info
                writeLine(cs, regular, 11, margin, y, "User: " + user.getFullName() + " (@" + user.getUsername() + ")");
                y -= 16;
                writeLine(cs, regular, 11, margin, y, "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                y -= 16;
                writeLine(cs, regular, 11, margin, y, "Cash Balance: Rs." + String.format("%.2f", user.getWalletBalance()));
                y -= 24;

                // Table header
                cs.setNonStrokingColor(new PDColor(new float[]{0.07f, 0.40f, 0.80f}, PDDeviceRGB.INSTANCE));
                cs.addRect(margin, y - 4, pageWidth - 2 * margin, 16);
                cs.fill();
                cs.setNonStrokingColor(new PDColor(new float[]{1f, 1f, 1f}, PDDeviceRGB.INSTANCE));
                String[] headers = {"Symbol", "Company", "Qty", "Avg Price", "Cur Price", "Value", "P&L"};
                float[] cols = {margin, 130, 280, 330, 390, 450, 510};
                for (int i = 0; i < headers.length; i++) {
                    writeLine(cs, bold, 9, cols[i], y, headers[i]);
                }
                y -= 20;

                // Table rows
                boolean alt = false;
                for (Portfolio.Holding h : portfolio.getHoldings().values()) {
                    if (alt) {
                        cs.setNonStrokingColor(new PDColor(new float[]{0.95f, 0.97f, 1f}, PDDeviceRGB.INSTANCE));
                        cs.addRect(margin, y - 4, pageWidth - 2 * margin, 14);
                        cs.fill();
                    }
                    cs.setNonStrokingColor(new PDColor(new float[]{0f, 0f, 0f}, PDDeviceRGB.INSTANCE));
                    String[] vals = {
                        h.getSymbol(), truncate(h.getCompanyName(), 18),
                        String.valueOf(h.getQuantity()),
                        String.format("%.2f", h.getAvgBuyPrice()),
                        String.format("%.2f", h.getCurrentPrice()),
                        String.format("%.2f", h.getCurrentValue()),
                        String.format("%+.2f", h.getProfitOrLoss())
                    };
                    // Color P&L
                    for (int i = 0; i < vals.length - 1; i++) {
                        writeLine(cs, regular, 9, cols[i], y, vals[i]);
                    }
                    if (h.getProfitOrLoss() >= 0)
                        cs.setNonStrokingColor(new PDColor(new float[]{0f, 0.6f, 0.2f}, PDDeviceRGB.INSTANCE));
                    else
                        cs.setNonStrokingColor(new PDColor(new float[]{0.8f, 0.1f, 0.1f}, PDDeviceRGB.INSTANCE));
                    writeLine(cs, bold, 9, cols[6], y, vals[6]);
                    cs.setNonStrokingColor(new PDColor(new float[]{0f, 0f, 0f}, PDDeviceRGB.INSTANCE));

                    y -= 16;
                    alt = !alt;
                    if (y < 80) break;
                }

                // Summary box
                y -= 10;
                cs.setNonStrokingColor(new PDColor(new float[]{0.95f, 0.95f, 0.95f}, PDDeviceRGB.INSTANCE));
                cs.addRect(margin, y - 50, pageWidth - 2 * margin, 60);
                cs.fill();
                cs.setNonStrokingColor(new PDColor(new float[]{0f, 0f, 0f}, PDDeviceRGB.INSTANCE));
                writeLine(cs, bold, 10, margin + 8, y, "SUMMARY");
                y -= 14;
                writeLine(cs, regular, 9, margin + 8, y, "Total Invested: Rs." + String.format("%.2f", portfolio.getTotalInvested()));
                writeLine(cs, regular, 9, margin + 200, y, "Total Value: Rs." + String.format("%.2f", portfolio.getTotalCurrentValue()));
                y -= 14;
                writeLine(cs, regular, 9, margin + 8, y, "Net P&L: Rs." + String.format("%+.2f", portfolio.getTotalProfitOrLoss()));
                writeLine(cs, regular, 9, margin + 200, y, "Overall Return: " + String.format("%.2f%%", portfolio.getOverallReturnPct()));

                // Footer
                cs.setNonStrokingColor(new PDColor(new float[]{0.5f, 0.5f, 0.5f}, PDDeviceRGB.INSTANCE));
                writeLine(cs, regular, 8, margin, 30, "TradeSphere Pro v1.0  |  Learn, Trade, Analyze.  |  Developed by: MOHAMMAD SAKIB AHMAD");
            }
            doc.save(filename);
        } catch (IOException e) { System.err.println("PDF export error: " + e.getMessage()); return null; }
        return filename;
    }

    private void writeLine(PDPageContentStream cs, PDType1Font font, float size,
                            float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    public String getReportsDir() { return REPORTS_DIR; }
}
