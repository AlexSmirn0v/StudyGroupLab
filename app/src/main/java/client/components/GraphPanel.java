package client.components;

import java.util.List;
import java.util.function.Consumer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GraphPanel extends JPanel {
    private final List<GraphNode> nodes = new ArrayList<>();
    private final List<GraphEdge> edges = new ArrayList<>();
    private GraphNode selectedNode;
    private final Consumer<GraphNode> clickHandler = GraphPanel::onGraphNodeClicked;

    public GraphPanel() {
        setPreferredSize(new Dimension(1200, 850));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initExampleGraph();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                GraphNode hit = findNodeAt(e.getPoint());
                selectedNode = hit;
                repaint();
                if (hit != null) {
                    clickHandler.accept(hit);
                }
            }
        });
    }

    private void initExampleGraph() {
        GraphNode r3135 = GraphNode.rectangle("R3135", 40, 35, 92, 62, new Color(255, 240, 196));
        GraphNode roman = GraphNode.circle("Roman", 230, 50, 64, new Color(220, 240, 210));
        GraphNode r3140 = GraphNode.rectangle("R3140", 330, 35, 92, 62, new Color(220, 240, 210));
        GraphNode anton = GraphNode.circle("Anton", 145, 210, 66, new Color(255, 236, 190));
        GraphNode r14 = GraphNode.rectangle("R1.4", 315, 220, 92, 62, new Color(255, 240, 196));

        nodes.addAll(List.of(r3135, roman, r3140, anton, r14));

        edges.add(new GraphEdge(anton, r3135));
        edges.add(new GraphEdge(roman, r3140));
        edges.add(new GraphEdge(anton, r14));
    }

    private GraphNode findNodeAt(Point p) {
        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (nodes.get(i).shape.contains(p)) {
                return nodes.get(i);
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(150, 160, 175));
        for (GraphEdge edge : edges) {
            drawArrow(g2, edge.from.center(), edge.to.center());
        }

        for (GraphNode node : nodes) {
            drawNode(g2, node, node == selectedNode);
        }

        g2.dispose();
    }

    private void drawNode(Graphics2D g2, GraphNode node, boolean selected) {
        Shape shape = node.shape;
        g2.setColor(node.fill);
        g2.fill(shape);

        g2.setColor(selected ? new Color(47, 111, 204) : new Color(192, 198, 208));
        g2.setStroke(new BasicStroke(selected ? 2.6f : 1.2f));
        g2.draw(shape);

        g2.setColor(new Color(20, 20, 20));
        FontMetrics fm = g2.getFontMetrics(getFont().deriveFont(Font.PLAIN, 14f));
        String text = node.label;
        int tx = (int) (node.bounds.x + (node.bounds.width - fm.stringWidth(text)) / 2);
        int ty = (int) (node.bounds.y + (node.bounds.height + fm.getAscent() - fm.getDescent()) / 2 - 2);
        g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        g2.drawString(text, tx, ty);
    }

    private void drawArrow(Graphics2D g2, Point from, Point to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double angle = Math.atan2(dy, dx);

        int startX = (int) (from.x + Math.cos(angle) * 22);
        int startY = (int) (from.y + Math.sin(angle) * 22);
        int endX = (int) (to.x - Math.cos(angle) * 22);
        int endY = (int) (to.y - Math.sin(angle) * 22);

        g2.drawLine(startX, startY, endX, endY);

        int arrowSize = 9;
        AffineTransform old = g2.getTransform();
        g2.translate(endX, endY);
        g2.rotate(angle);

        Path2D arrowHead = new Path2D.Double();
        arrowHead.moveTo(0, 0);
        arrowHead.lineTo(-arrowSize, -arrowSize / 2.0);
        arrowHead.lineTo(-arrowSize, arrowSize / 2.0);
        arrowHead.closePath();

        g2.fill(arrowHead);
        g2.setTransform(old);
    }

    static void onGraphNodeClicked(GraphNode node) {
        // Empty business-logic handler.
        // Put selection, details panel updates, navigation, etc. here.
        System.out.println("Clicked node: " + node.label);
    }

    static class GraphNode {
        final String label;
        final Rectangle2D.Double bounds;
        final Shape shape;
        final Color fill;

        private GraphNode(String label, Rectangle2D.Double bounds, Shape shape, Color fill) {
            this.label = label;
            this.bounds = bounds;
            this.shape = shape;
            this.fill = fill;
        }

        static GraphNode rectangle(String label, double x, double y, double w, double h, Color fill) {
            Rectangle2D.Double bounds = new Rectangle2D.Double(x, y, w, h);
            return new GraphNode(label, bounds, bounds, fill);
        }

        static GraphNode circle(String label, double x, double y, double d, Color fill) {
            Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, d, d);
            Rectangle2D.Double bounds = new Rectangle2D.Double(x, y, d, d);
            return new GraphNode(label, bounds, ellipse, fill);
        }

        Point center() {
            return new Point((int) (bounds.x + bounds.width / 2), (int) (bounds.y + bounds.height / 2));
        }
    }

    static class GraphEdge {
        final GraphNode from;
        final GraphNode to;

        GraphEdge(GraphNode from, GraphNode to) {
            this.from = from;
            this.to = to;
        }
    }
}
