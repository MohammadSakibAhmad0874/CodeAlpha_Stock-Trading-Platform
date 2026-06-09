package com.tradesphere.services;

import javafx.scene.Scene;
import java.util.prefs.Preferences;

/**
 * Manages application theme (dark/light) with instant CSS switching.
 */
public class ThemeManager {

    public enum Theme { DARK, LIGHT }

    private static ThemeManager instance;
    private Theme currentTheme = Theme.DARK;
    private Scene scene;
    private final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);

    private static final String DARK_CSS  = "/css/dark-theme.css";
    private static final String LIGHT_CSS = "/css/light-theme.css";
    private static final String PREF_KEY  = "tradesphere_theme";

    private ThemeManager() {
        String saved = prefs.get(PREF_KEY, "DARK");
        currentTheme = "LIGHT".equals(saved) ? Theme.LIGHT : Theme.DARK;
    }

    public static ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        apply();
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        prefs.put(PREF_KEY, theme.name());
        apply();
    }

    public void toggleTheme() {
        setTheme(currentTheme == Theme.DARK ? Theme.LIGHT : Theme.DARK);
    }

    public Theme getCurrentTheme() { return currentTheme; }
    public boolean isDark() { return currentTheme == Theme.DARK; }

    private void apply() {
        if (scene == null) return;
        scene.getStylesheets().clear();
        String css = currentTheme == Theme.DARK ? DARK_CSS : LIGHT_CSS;
        var resource = getClass().getResource(css);
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.err.println("⚠️ CSS not found: " + css);
        }
    }

    public String getCurrentCssPath() {
        return currentTheme == Theme.DARK ? DARK_CSS : LIGHT_CSS;
    }
}
