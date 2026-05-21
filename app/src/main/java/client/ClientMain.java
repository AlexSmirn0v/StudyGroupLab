package client;

import java.awt.Color;
import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import client.components.LoginFrame;

public class ClientMain {
    public static void main(String[] args) {
        try (ConnectFacade connector = new ConnectFacade()) {
            installModernBaseStyle();
            AppContext context = new AppContext(connector);
            context.startPeriodicRefresh(3000);
            SwingUtilities.invokeLater(() -> new LoginFrame(context).setVisible(true));
        } catch (IOException e) {
            System.out.println("Не удалось подключиться к серверу");
        }
    }

    private static void installModernBaseStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("Panel.background", new Color(246, 248, 252));
        UIManager.put("ScrollPane.background", Color.WHITE);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(230, 235, 242));
        UIManager.put("Table.selectionBackground", new Color(212, 228, 255));
        UIManager.put("Table.selectionForeground", Color.BLACK);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.inactiveBackground", Color.WHITE);
        UIManager.put("TextField.border", new EmptyBorder(0, 0, 0, 0));
    }
}
