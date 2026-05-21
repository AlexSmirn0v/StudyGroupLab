package client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

import client.AppLocale.Localizable;

public class AppContext {
    private final ConnectFacade connectFacade;
    private AppLocale locale = AppLocale.findLocale(Locale.getDefault());
    private final HashMap<String, Color> colors;
    private Timer refreshTimer;
    private String username = "Anonymous";
    private String password = "";
    private final List<Updatable> updatables = new ArrayList<>();
    private final List<Localizable> localizables = new ArrayList<>();

    public AppContext(ConnectFacade connectFacade) {
        this.connectFacade = connectFacade;
        colors = createDefaultColors();
    }

    private HashMap<String, Color> createDefaultColors() {
        HashMap<String, Color> defaultColors = new HashMap<>();
        defaultColors.put("bg", new Color(246, 248, 252));
        defaultColors.put("fieldBorder", new Color(200, 208, 220));
        defaultColors.put("scrollBorder", new Color(214, 221, 233));
        defaultColors.put("muted", new Color(100, 108, 120));
        defaultColors.put("error", new Color(196, 58, 58));
        defaultColors.put("success", new Color(36, 128, 72));
        return defaultColors;
    }

    public ConnectFacade getConnectFacade() {
        return connectFacade;
    }

    public AppLocale getLocale() {
        return locale;
    }

    public String getLocalText(String key) {
        Map<String, String> uiText = new HashMap<>();
        uiText.putAll(locale.labels);
        return uiText.getOrDefault(key, key);
    }

    public void setLocale(AppLocale locale) {
        this.locale = locale;
        notifyLocale();
    }

    public Color getColor(String key) {
        return colors.get(key);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public JPanel getRoot() {
        for (Updatable updatable : updatables) {
            if (updatable instanceof JPanel panel) {
                return panel;
            }
        }
        return null;
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void registerUpdatable(Updatable updatable) {
        updatables.add(updatable);
    }

    public void unregisterUpdatable(Updatable updatable) {
        updatables.remove(updatable);
    }

    public void notifyUpdate() {
        for (Updatable updatable : updatables) {
            updatable.update();
        }
    }

    public void registerLocalizable(Localizable localizable) {
        localizables.add(localizable);
    }

    public void unregisterLocalizable(Localizable localizable) {
        localizables.remove(localizable);
    }

    public void notifyLocale() {
        for (Localizable localizable : localizables) {
            localizable.applyLocale(this.locale);
        }
    }

    public void startPeriodicRefresh(int intervalMs) {
        if (refreshTimer != null) {
            return;
        }
        refreshTimer = new Timer(intervalMs, e -> notifyUpdate());
        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }

    public void stopPeriodicRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    /**
     * Interface for components that need to be notified of data updates.
     */
    public interface Updatable {
        void update();
    }
}
