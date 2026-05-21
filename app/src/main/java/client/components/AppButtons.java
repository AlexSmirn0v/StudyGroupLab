package client.components;

import java.awt.Cursor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.function.Supplier;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import client.AppLocale;

public class AppButtons {
    public static class RoundedButton extends JButton {
        private Supplier<String> keySupplier;

        public RoundedButton() {
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        public void setKeySupplier(Supplier<String> keySupplier) {
            this.keySupplier = keySupplier;
            updateText();
        }

        @Override
        public void repaint() {
            super.repaint();
            updateText();
        }

        public void updateText() {
            if (keySupplier != null) {
                setText(keySupplier.get());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ButtonModel model = getModel();
            Color bg = model.isPressed() ? new Color(196, 218, 255)
                    : model.isRollover() ? new Color(230, 238, 249)
                            : Color.WHITE;

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

            g2.setColor(new Color(188, 197, 209));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

            g2.setColor(new Color(28, 35, 44));
            FontMetrics fm = g2.getFontMetrics();
            Insets insets = getInsets();
            String text = getText();
            int tx = insets.left + 14;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1;
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }

    public static class RoundLocaleButton extends JButton {
        private AppLocale localeOption = AppLocale.RU;

        public RoundLocaleButton() {
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(34, 34));
            setMinimumSize(new Dimension(34, 34));
            setMaximumSize(new Dimension(34, 34));
            setToolTipText("Switch locale");
        }

        public void setLocaleOption(AppLocale localeOption) {
            this.localeOption = localeOption;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color outer = switch (localeOption) {
                case RU -> new Color(67, 94, 186);
                case NO -> new Color(163, 27, 35);
                case DA -> new Color(176, 54, 58);
                case ES_CR -> new Color(36, 85, 170);
            };

            g2.setColor(outer);
            g2.fillOval(1, 1, getWidth() - 3, getHeight() - 3);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD, 11f));
            String code = switch (localeOption) {
                case RU -> "RU";
                case NO -> "NO";
                case DA -> "DA";
                case ES_CR -> "CR";
            };
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(code)) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1;
            g2.drawString(code, tx, ty);

            g2.dispose();
        }
    }

    public static class TabToggleButton extends JToggleButton {
        public TabToggleButton(String text) {
            super(text);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(getFont().deriveFont(Font.BOLD, 14f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(95, 32));
            setMargin(new Insets(4, 14, 4, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected()) {
                g2.setColor(new Color(38, 129, 255));
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(225, 232, 241));
            } else {
                g2.setColor(new Color(236, 241, 248));
            }

            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.setColor(isSelected() ? Color.WHITE : new Color(68, 75, 87));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(getText())) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1;
            g2.drawString(getText(), tx, ty);

            g2.dispose();
        }
    }
}
