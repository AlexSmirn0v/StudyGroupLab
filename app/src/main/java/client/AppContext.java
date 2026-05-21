package client;

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;

public class AppContext {
    private final ConnectFacade connectFacade;
    private AppLocale locale = AppLocale.findLocale(Locale.getDefault());
    private final HashMap<String, Color> colors;
    private String username = "Anonymous";
    private String password = "";

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

    public void setLocale(AppLocale locale) {
        this.locale = locale;
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

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
