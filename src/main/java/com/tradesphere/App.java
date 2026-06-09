package com.tradesphere;

import com.tradesphere.database.DatabaseManager;
import com.tradesphere.services.*;
import com.tradesphere.view.LandingScreen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * TradeSphere Pro – Main Application Entry Point
 * "Learn, Trade, Analyze."
 *
 * @author  MOHAMMAD SAKIB AHMAD
 * @version 1.0
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // ── Init database ──────────────────────────────────────────────────
        DatabaseManager.getInstance().init();

        // ── Set up notifications ───────────────────────────────────────────
        NotificationManager.getInstance().setStage(stage);

        // ── Build landing scene ────────────────────────────────────────────
        LandingScreen landing = new LandingScreen();
        Scene scene = new Scene(landing.getRoot(), 1280, 760);

        // ── Apply theme ────────────────────────────────────────────────────
        ThemeManager.getInstance().setScene(scene);

        // ── Stage setup ────────────────────────────────────────────────────
        stage.setScene(scene);
        stage.setTitle("TradeSphere Pro – Virtual Stock Trading Platform");
        stage.setMinWidth(1100);
        stage.setMinHeight(680);
        stage.setMaximized(false);

        // Try to load icon if present
        try {
            stage.getIcons().add(new Image(
                getClass().getResourceAsStream("/assets/icon.png")));
        } catch (Exception ignored) {}

        stage.show();
        stage.centerOnScreen();

        // ── Start market simulator ─────────────────────────────────────────
        MarketSimulator.getInstance().start();

        // ── Graceful shutdown ──────────────────────────────────────────────
        stage.setOnCloseRequest(e -> shutdown());
    }

    private void shutdown() {
        MarketSimulator.getInstance().stop();
        DatabaseManager.getInstance().close();
        Platform.exit();
        System.exit(0);
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) {
        launch(args);
    }
}
