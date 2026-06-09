package com.tradesphere.view;

import com.tradesphere.App;
import com.tradesphere.controller.AuthController;
import com.tradesphere.services.NotificationManager;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Login / Register / Forgot-Password screen.
 */
public class LoginScreen {

    private final StackPane root = new StackPane();

    public LoginScreen() { build(); }

    private void build() {
        root.setStyle("-fx-background-color: #0a0c12;");

        // Animated BG canvas
        Canvas canvas = new Canvas(1280, 760);
        drawBg(canvas);

        // Center card
        VBox card = buildCard();
        StackPane.setAlignment(card, Pos.CENTER);

        root.getChildren().addAll(canvas, card);

        // Entrance animation
        FadeTransition ft = new FadeTransition(Duration.millis(500), card);
        ft.setFromValue(0); ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
        tt.setFromY(30); tt.setToY(0);
        new ParallelTransition(ft, tt).play();
    }

    private VBox buildCard() {
        VBox card = new VBox(0);
        card.setMaxWidth(460);
        card.setMinWidth(440);
        card.setStyle("-fx-background-color: #141820; -fx-background-radius: 20; " +
            "-fx-border-color: #252d42; -fx-border-width: 1; -fx-border-radius: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 40, 0, 0, 10);");

        // Header
        VBox header = new VBox(6);
        header.setPadding(new Insets(36, 36, 24, 36));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1a2744, #141820); " +
            "-fx-background-radius: 20 20 0 0;");

        Label logo = new Label("🌐 TradeSphere Pro");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        logo.setStyle("-fx-text-fill: #3b82f6;");

        Label tagline = new Label("Learn, Trade, Analyze.");
        tagline.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        header.getChildren().addAll(logo, tagline);

        // Tab Pane
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: transparent;");

        Tab loginTab    = new Tab("  Sign In  ",  buildLoginForm());
        Tab registerTab = new Tab("  Register  ", buildRegisterForm());
        Tab forgotTab   = new Tab("  Forgot Password  ", buildForgotForm());

        tabs.getTabs().addAll(loginTab, registerTab, forgotTab);
        tabs.getSelectionModel().selectFirst();

        card.getChildren().addAll(header, tabs);
        return card;
    }

    // ─── Login Form ───────────────────────────────────────────────────────────

    private VBox buildLoginForm() {
        VBox form = new VBox(16);
        form.setPadding(new Insets(30, 36, 36, 36));
        form.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Welcome back 👋");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #e8eaed;");

        Label subtitle = new Label("Sign in to your TradeSphere account");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        // Username
        VBox userBox = fieldBox("Username", "Enter your username");
        TextField userField = (TextField) ((VBox) userBox).getChildren().get(1);

        // Password
        VBox passBox = passwordBox("Password");
        PasswordField passField = (PasswordField) ((HBox) ((VBox) passBox).getChildren().get(1)).getChildren().get(0);
        TextField passVisible  = (TextField)       ((HBox) ((VBox) passBox).getChildren().get(1)).getChildren().get(1);
        Button showPassBtn     = (Button)           ((HBox) ((VBox) passBox).getChildren().get(1)).getChildren().get(2);

        // Remember + forgot
        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER_LEFT);
        CheckBox remember = new CheckBox("Remember me");
        remember.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button forgot = new Button("Forgot password?");
        forgot.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b82f6; " +
            "-fx-cursor: hand; -fx-font-size: 12px; -fx-underline: true; -fx-padding: 0;");
        bottom.getChildren().addAll(remember, sp, forgot);

        // Error label
        Label errLabel = new Label();
        errLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errLabel.setVisible(false);

        // Login button
        Button loginBtn = new Button("Sign In →");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        loginBtn.setPadding(new Insets(13, 0, 13, 0));

        // Demo account hint
        Label demo = new Label("Demo: username = demo  |  password = demo123");
        demo.setStyle("-fx-text-fill: #334155; -fx-font-size: 11px;");
        demo.setAlignment(Pos.CENTER);

        // Show/hide password toggle
        showPassBtn.setOnAction(e -> {
            boolean showing = passVisible.isVisible();
            passVisible.setVisible(!showing);
            passField.setVisible(showing);
            showPassBtn.setText(showing ? "👁" : "🙈");
        });

        // Login action
        Runnable doLogin = () -> {
            String username = userField.getText();
            String password = passField.isVisible() ? passField.getText() : passVisible.getText();
            AuthController.LoginResult result = AuthController.getInstance().login(username, password);
            if (result.success()) {
                navigateToDashboard();
            } else {
                errLabel.setText("⚠  " + result.error());
                errLabel.setVisible(true);
                shake(loginBtn);
            }
        };
        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        passVisible.setOnAction(e -> doLogin.run());

        // Create demo account
        ensureDemoAccount();

        form.getChildren().addAll(title, subtitle, userBox, passBox, bottom, errLabel, loginBtn, demo);
        return form;
    }

    // ─── Register Form ───────────────────────────────────────────────────────

    private VBox buildRegisterForm() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(30, 36, 36, 36));
        form.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Create Account 🎉");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #e8eaed;");
        Label subtitle = new Label("Get ₹1,00,000 virtual cash instantly");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        VBox nameBox  = fieldBox("Full Name", "Your full name");
        VBox emailBox = fieldBox("Email", "your@email.com");
        VBox userBox  = fieldBox("Username", "Choose a username");
        VBox passBox  = fieldBox("Password", "Min. 6 characters");

        TextField nameField  = (TextField) ((VBox) nameBox).getChildren().get(1);
        TextField emailField = (TextField) ((VBox) emailBox).getChildren().get(1);
        TextField userField  = (TextField) ((VBox) userBox).getChildren().get(1);
        TextField passField  = new PasswordField();
        passField.setPromptText("Min. 6 characters");
        passField.getStyleClass().add("text-field");
        passField.setMaxWidth(Double.MAX_VALUE);
        ((VBox) passBox).getChildren().set(1, passField);

        Label errLabel = new Label();
        errLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errLabel.setVisible(false);

        Label successLabel = new Label();
        successLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 12px;");
        successLabel.setVisible(false);

        Button regBtn = new Button("Create Account");
        regBtn.getStyleClass().add("btn-success");
        regBtn.setMaxWidth(Double.MAX_VALUE);
        regBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        regBtn.setPadding(new Insets(13, 0, 13, 0));

        regBtn.setOnAction(e -> {
            String pass = ((PasswordField) ((VBox) passBox).getChildren().get(1)).getText();
            AuthController.RegisterResult res = AuthController.getInstance().register(
                nameField.getText(), emailField.getText(), userField.getText(), pass);
            if (res.success()) {
                successLabel.setText("✅ Account created! Please sign in.");
                successLabel.setVisible(true);
                errLabel.setVisible(false);
                nameField.clear(); emailField.clear(); userField.clear();
                ((PasswordField) ((VBox) passBox).getChildren().get(1)).clear();
                NotificationManager.getInstance().success("Welcome!", "Account created. Sign in to start trading.");
            } else {
                errLabel.setText("⚠  " + res.error());
                errLabel.setVisible(true);
                successLabel.setVisible(false);
                shake(regBtn);
            }
        });

        form.getChildren().addAll(title, subtitle, nameBox, emailBox, userBox, passBox, errLabel, successLabel, regBtn);
        return form;
    }

    // ─── Forgot Password Form ────────────────────────────────────────────────

    private VBox buildForgotForm() {
        VBox form = new VBox(16);
        form.setPadding(new Insets(30, 36, 36, 36));
        form.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Reset Password 🔐");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #e8eaed;");
        Label sub = new Label("Enter your email to recover your account");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        VBox emailBox = fieldBox("Email Address", "your@email.com");
        TextField emailField = (TextField) ((VBox) emailBox).getChildren().get(1);

        Label msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);

        Button recoverBtn = new Button("Send Recovery Info");
        recoverBtn.getStyleClass().add("btn-primary");
        recoverBtn.setMaxWidth(Double.MAX_VALUE);
        recoverBtn.setPadding(new Insets(13, 0, 13, 0));

        recoverBtn.setOnAction(e -> {
            AuthController.RecoveryResult res = AuthController.getInstance().recoverPassword(emailField.getText());
            msgLabel.setText(res.success() ? "✅ " + res.message() : "⚠  " + res.message());
            msgLabel.setStyle(res.success() ? "-fx-text-fill: #22c55e; -fx-font-size: 13px;"
                                             : "-fx-text-fill: #ef4444; -fx-font-size: 13px;");
            msgLabel.setVisible(true);
        });

        form.getChildren().addAll(title, sub, emailBox, msgLabel, recoverBtn);
        return form;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private VBox fieldBox(String labelText, String prompt) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("text-field");
        field.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private VBox passwordBox(String labelText) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");

        HBox row = new HBox(0);
        PasswordField pass = new PasswordField();
        pass.setPromptText("Enter your password");
        pass.getStyleClass().add("text-field");
        HBox.setHgrow(pass, Priority.ALWAYS);

        TextField visible = new TextField();
        visible.setPromptText("Enter your password");
        visible.getStyleClass().add("text-field");
        visible.setVisible(false);
        visible.setManaged(false);
        HBox.setHgrow(visible, Priority.ALWAYS);

        Button toggle = new Button("👁");
        toggle.setStyle("-fx-background-color: #1e2436; -fx-text-fill: #94a3b8; " +
            "-fx-border-color: #252d42; -fx-border-width: 1.5 1.5 1.5 0; " +
            "-fx-background-radius: 0 10 10 0; -fx-border-radius: 0 10 10 0; " +
            "-fx-padding: 10 12; -fx-cursor: hand;");
        pass.setStyle("-fx-background-radius: 10 0 0 10; -fx-border-radius: 10 0 0 10;");

        // Sync fields
        pass.textProperty().addListener((o,ov,nv) -> visible.setText(nv));
        visible.textProperty().addListener((o,ov,nv) -> pass.setText(nv));

        row.getChildren().addAll(pass, visible, toggle);
        box.getChildren().addAll(lbl, row);
        return box;
    }

    private void shake(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setFromX(0); tt.setByX(8); tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.play();
    }

    private void ensureDemoAccount() {
        if (com.tradesphere.database.DatabaseManager.getInstance().findUserByUsername("demo") == null) {
            AuthController.getInstance().register("Demo User", "demo@tradesphere.pro", "demo", "demo123");
        }
    }

    private void navigateToDashboard() {
        Stage stage = App.getPrimaryStage();
        MainWindow main = new MainWindow();
        stage.getScene().setRoot(main.getRoot());
        stage.setMaximized(true);
        FadeTransition ft = new FadeTransition(Duration.millis(500), main.getRoot());
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    private void drawBg(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#0a0c12")), new Stop(1, Color.web("#0d1b35"))));
        gc.fillRect(0,0,1280,760);
        // Glow orb
        gc.setFill(new RadialGradient(0,0,0.5,0.5,0.6,true,CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(59,130,246,0.08)), new Stop(1, Color.TRANSPARENT)));
        gc.fillOval(200,100,800,600);
    }

    public Parent getRoot() { return root; }
}
