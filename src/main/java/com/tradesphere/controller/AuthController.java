package com.tradesphere.controller;

import com.tradesphere.database.DatabaseManager;
import com.tradesphere.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Handles registration, login, and password recovery logic.
 */
public class AuthController {

    private static AuthController instance;
    private AuthController() {}

    public static AuthController getInstance() {
        if (instance == null) instance = new AuthController();
        return instance;
    }

    // ─── Registration ─────────────────────────────────────────────────────────

    public record RegisterResult(boolean success, String error) {}

    public RegisterResult register(String fullName, String email, String username, String password) {
        // Validation
        if (fullName == null || fullName.isBlank())    return new RegisterResult(false, "Full name is required.");
        if (email == null || !email.contains("@"))      return new RegisterResult(false, "Invalid email address.");
        if (username == null || username.length() < 3)  return new RegisterResult(false, "Username must be at least 3 characters.");
        if (password == null || password.length() < 6)  return new RegisterResult(false, "Password must be at least 6 characters.");

        // Uniqueness check
        if (DatabaseManager.getInstance().findUserByUsername(username) != null)
            return new RegisterResult(false, "Username '" + username + "' is already taken.");
        if (DatabaseManager.getInstance().findUserByEmail(email) != null)
            return new RegisterResult(false, "Email is already registered.");

        // Create user
        String id   = UUID.randomUUID().toString();
        String hash = hashPassword(password);
        User user   = new User(id, fullName.trim(), email.trim().toLowerCase(),
                               username.trim().toLowerCase(), hash);
        boolean saved = DatabaseManager.getInstance().saveUser(user);
        return saved
            ? new RegisterResult(true, null)
            : new RegisterResult(false, "Database error. Please try again.");
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    public record LoginResult(boolean success, String error, User user) {}

    public LoginResult login(String username, String password) {
        if (username == null || username.isBlank()) return new LoginResult(false, "Username is required.", null);
        if (password == null || password.isBlank()) return new LoginResult(false, "Password is required.", null);

        User user = DatabaseManager.getInstance().findUserByUsername(username.trim().toLowerCase());
        if (user == null)
            return new LoginResult(false, "Account not found. Please register first.", null);

        if (!user.getPasswordHash().equals(hashPassword(password)))
            return new LoginResult(false, "Incorrect password. Please try again.", null);

        AppController.getInstance().login(user);
        return new LoginResult(true, null, user);
    }

    // ─── Forgot Password (Simulated) ──────────────────────────────────────────

    public record RecoveryResult(boolean success, String message) {}

    public RecoveryResult recoverPassword(String email) {
        if (email == null || !email.contains("@"))
            return new RecoveryResult(false, "Please enter a valid email address.");

        User user = DatabaseManager.getInstance().findUserByEmail(email.trim().toLowerCase());
        if (user == null)
            return new RecoveryResult(false, "No account found with this email.");

        // Simulate sending recovery email
        return new RecoveryResult(true,
            "Recovery instructions have been sent to " + email + ".\n" +
            "Your username is: " + user.getUsername());
    }

    // ─── Password Hashing ─────────────────────────────────────────────────────

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password; // fallback (should never happen)
        }
    }

    public boolean validatePassword(String raw, String hash) {
        return hashPassword(raw).equals(hash);
    }
}
