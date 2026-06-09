package com.tradesphere.services;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages toast-style notification popups.
 * Queues notifications and shows them one at a time in the top-right corner.
 */
public class NotificationManager {

    public enum Type { SUCCESS, ERROR, WARNING, INFO }

    private static NotificationManager instance;
    private Stage primaryStage;
    private final Queue<NotificationItem> queue = new LinkedList<>();
    private boolean showing = false;

    private record NotificationItem(String title, String message, Type type) {}

    private NotificationManager() {}

    public static NotificationManager getInstance() {
        if (instance == null) instance = new NotificationManager();
        return instance;
    }

    public void setStage(Stage stage) { this.primaryStage = stage; }

    // ─── Public API ───────────────────────────────────────────────────────────

    public void success(String title, String message) { enqueue(title, message, Type.SUCCESS); }
    public void error(String title, String message)   { enqueue(title, message, Type.ERROR);   }
    public void warning(String title, String message) { enqueue(title, message, Type.WARNING); }
    public void info(String title, String message)    { enqueue(title, message, Type.INFO);    }

    private void enqueue(String title, String message, Type type) {
        queue.add(new NotificationItem(title, message, type));
        if (!showing) showNext();
    }

    private void showNext() {
        if (queue.isEmpty() || primaryStage == null) { showing = false; return; }
        showing = true;
        NotificationItem item = queue.poll();
        showToast(item);
    }

    // ─── Toast Builder ────────────────────────────────────────────────────────

    private void showToast(NotificationItem item) {
        Popup popup = new Popup();

        // Container
        VBox box = new VBox(4);
        box.setPadding(new Insets(14, 18, 14, 18));
        box.setMaxWidth(320);
        box.setMinWidth(280);
        box.setStyle(getStyle(item.type()));

        // Icon + Title row
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(getIcon(item.type()));
        icon.setFont(Font.font(16));
        icon.setTextFill(Color.WHITE);

        Label title = new Label(item.title());
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setTextFill(Color.WHITE);

        header.getChildren().addAll(icon, title);

        Label msg = new Label(item.message());
        msg.setFont(Font.font("System", 12));
        msg.setTextFill(Color.rgb(220, 220, 220));
        msg.setWrapText(true);

        // Progress bar
        Region progress = new Region();
        progress.setPrefHeight(3);
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-radius: 2;");

        box.getChildren().addAll(header, msg, progress);
        popup.getContent().add(box);
        popup.setAutoFix(true);

        // Position: top-right of primary stage
        double x = primaryStage.getX() + primaryStage.getWidth() - 340;
        double y = primaryStage.getY() + 60;
        popup.show(primaryStage, x, y);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), box);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        // Progress animation (shrinks bar to 0)
        Timeline progressAnim = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progress.prefWidthProperty(), 284)),
            new KeyFrame(Duration.millis(3500), new KeyValue(progress.prefWidthProperty(), 0))
        );

        // Slide in from right
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), box);
        slideIn.setFromX(60); slideIn.setToX(0);

        // Fade out after 3.5s
        PauseTransition pause = new PauseTransition(Duration.millis(3500));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), box);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> { popup.hide(); showNext(); });

        new ParallelTransition(fadeIn, slideIn).play();
        progressAnim.play();
        pause.setOnFinished(e -> fadeOut.play());
        pause.play();
    }

    private String getStyle(Type type) {
        String color = switch (type) {
            case SUCCESS -> "linear-gradient(from 0% 0% to 100% 100%, #1a7a4a, #22c55e)";
            case ERROR   -> "linear-gradient(from 0% 0% to 100% 100%, #7a1a1a, #ef4444)";
            case WARNING -> "linear-gradient(from 0% 0% to 100% 100%, #7a5c1a, #f59e0b)";
            case INFO    -> "linear-gradient(from 0% 0% to 100% 100%, #1a3a7a, #3b82f6)";
        };
        return "-fx-background-color: " + color + "; " +
               "-fx-background-radius: 12; " +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 4);";
    }

    private String getIcon(Type type) {
        return switch (type) {
            case SUCCESS -> "✅";
            case ERROR   -> "❌";
            case WARNING -> "⚠️";
            case INFO    -> "ℹ️";
        };
    }
}
