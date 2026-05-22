package client.components;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.AppContext;
import model.StudyGroup;
import model.CommandMessage;
import model.CommandFormat;

public class GraphPanel extends JPanel implements AppContext.Updatable {
    private final AppContext appContext;
    private final List<GraphNode> nodes = new ArrayList<>();
    private final List<GraphEdge> edges = new ArrayList<>();
    private final List<StudyGroup> currentGroups = new ArrayList<>();
    private GraphNode selectedNode;
    private GraphNode draggedNode;
    private Point dragOffset;
    private final Map<String, Color> userColorMap = new HashMap<>();
    private static final Color[] USER_COLORS = {
        new Color(255, 107, 107),
        new Color(107, 182, 255),
        new Color(255, 182, 107),
        new Color(155, 107, 255)
    };
    private final Consumer<GraphNode> clickHandler = GraphPanel::onGraphNodeClicked;

    public GraphPanel(AppContext appContext) {
        this.appContext = appContext;
        setPreferredSize(new Dimension(1200, 850));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        setupMouseListeners();
        appContext.registerUpdatable(this);
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                GraphNode hit = findNodeAt(e.getPoint());
                if (hit != null && hit.isDraggable) {
                    draggedNode = hit;
                    dragOffset = new Point(
                        e.getX() - (int)draggedNode.bounds.x,
                        e.getY() - (int)draggedNode.bounds.y
                    );
                }
                selectedNode = hit;
                repaint();
                if (hit != null) {
                    clickHandler.accept(hit);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
                dragOffset = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null && dragOffset != null) {
                    int newX = e.getX() - dragOffset.x;
                    int newY = e.getY() - dragOffset.y;
                    
                    draggedNode.bounds.x = Math.max(0, Math.min(newX, getWidth() - draggedNode.bounds.width));
                    draggedNode.bounds.y = Math.max(0, Math.min(newY, getHeight() - draggedNode.bounds.height));
                    
                    updateShape(draggedNode);
                    repaint();
                }
            }
        });
    }

    private void updateShape(GraphNode node) {
        if (node.shapeType == ShapeType.CIRCLE) {
            node.shape = new Ellipse2D.Double(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
        } else {
            node.shape = new Rectangle2D.Double(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
        }
    }

    @Override
    public void update() {
        try {
            refreshFromServer();
        } catch (Exception e) {
            System.err.println("Failed to refresh graph: " + e.getMessage());
        }
    }

    private void refreshFromServer() throws Exception {
        if (appContext.getConnectFacade() == null) {
            return;
        }

        CommandMessage message = new CommandMessage(
            CommandFormat.SHOW,
            appContext.getUsername(),
            appContext.getPassword()
        );

        Collection<StudyGroup> groups = appContext.getConnectFacade().askStudyGroup(message)
            .stream()
            .filter(group -> group.getName().toLowerCase().contains(appContext.getSearchQuery().toLowerCase()))
            .collect(Collectors.toList());

        currentGroups.clear();
        currentGroups.addAll(groups);

        if (groups != null) {
            buildGraph(groups);
            repaint();
        }
    }

    private void buildGraph(Collection<StudyGroup> groups) {
        nodes.clear();
        edges.clear();
        userColorMap.clear();

        Map<String, List<StudyGroup>> groupsByUser = groups.stream()
            .collect(Collectors.groupingBy(
                group -> group.getAuthorName() != null ? group.getAuthorName() : "Unknown",
                Collectors.toList()
            ));

        int colorIndex = 0;
        for (String user : groupsByUser.keySet()) {
            userColorMap.put(user, USER_COLORS[colorIndex % USER_COLORS.length]);
            colorIndex++;
        }

        int userY = 50;
        int userSpacing = 200;
        int groupY = 250;
        int groupSpacing = 150;

        for (Map.Entry<String, List<StudyGroup>> entry : groupsByUser.entrySet()) {
            String user = entry.getKey();
            List<StudyGroup> userGroups = entry.getValue();
            Color userColor = userColorMap.get(user);

            double userX = 50 + groupsByUser.keySet().size() * userSpacing / 2;
            if (groupsByUser.size() > 1) {
                int userIndex = new ArrayList<>(groupsByUser.keySet()).indexOf(user);
                userX = 100 + userIndex * userSpacing;
            }
            
            GraphNode userNode = GraphNode.circle(user, userX, userY, 60, userColor, false);
            nodes.add(userNode);

            int groupX = (int)userX - (userGroups.size() * groupSpacing / 2);
            for (StudyGroup group : userGroups) {
                long studentCount = group.getStudentsCount() != null ? group.getStudentsCount() : 10;
                double groupSize = Math.max(40, Math.min(120, 40 + studentCount * 5));

                double gx = group.getCoordinates() != null && group.getCoordinates().getX() != null
                    ? group.getCoordinates().getX() * 0.5 + 150
                    : groupX;
                double gy = group.getCoordinates() != null
                    ? group.getCoordinates().getY() * 0.5 + groupY
                    : groupY;

                GraphNode groupNode = GraphNode.circle(
                    group.getName(),
                    gx,
                    gy,
                    groupSize,
                    new Color(
                        (userColor.getRed() + 255) / 2,
                        (userColor.getGreen() + 255) / 2,
                        (userColor.getBlue() + 255) / 2
                    ),
                    true
                );
                groupNode.groupId = group.getId();
                nodes.add(groupNode);

                edges.add(new GraphEdge(userNode, groupNode));

                groupX += groupSpacing;
            }
        }
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

        g2.setColor(selected ? new Color(47, 111, 204) : new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(selected ? 3.0f : 1.5f));
        g2.draw(shape);

        g2.setColor(new Color(20, 20, 20));
        FontMetrics fm = g2.getFontMetrics(getFont().deriveFont(Font.BOLD, 12f));
        String text = node.label;
        int tx = (int) (node.bounds.x + (node.bounds.width - fm.stringWidth(text)) / 2);
        int ty = (int) (node.bounds.y + (node.bounds.height + fm.getAscent() - fm.getDescent()) / 2 - 2);
        g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString(text, tx, ty);
    }

    private void drawArrow(Graphics2D g2, Point from, Point to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < 1) return;
        
        double angle = Math.atan2(dy, dx);
        
        int startX = (int) (from.x + Math.cos(angle) * 30);
        int startY = (int) (from.y + Math.sin(angle) * 30);
        int endX = (int) (to.x - Math.cos(angle) * 30);
        int endY = (int) (to.y - Math.sin(angle) * 30);

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
        if(node.groupId != null) {
            
        } else {
            System.out.println("Clicked user node: " + node.label);
        }
        System.out.println("Clicked node: " + node.label + (node.groupId != null ? " (Group ID: " + node.groupId + ")" : " (User)"));
    }

    enum ShapeType {
        CIRCLE, RECTANGLE
    }

    static class GraphNode {
        final String label;
        final Rectangle2D.Double bounds;
        Shape shape;
        final Color fill;
        final ShapeType shapeType;
        final boolean isDraggable;
        Long groupId;

        private GraphNode(String label, Rectangle2D.Double bounds, Shape shape, Color fill, ShapeType shapeType, boolean isDraggable) {
            if (label.length() > 5) {
                this.label = label.substring(0, 5) + "...";
            } else {
                this.label = label;
            }
            this.bounds = bounds;
            this.shape = shape;
            this.fill = fill;
            this.shapeType = shapeType;
            this.isDraggable = isDraggable;
            this.groupId = null;
        }

        static GraphNode circle(String label, double x, double y, double d, Color fill, boolean draggable) {
            Rectangle2D.Double bounds = new Rectangle2D.Double(x, y, d, d);
            Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, d, d);
            return new GraphNode(label, bounds, ellipse, fill, ShapeType.CIRCLE, draggable);
        }

        static GraphNode rectangle(String label, double x, double y, double w, double h, Color fill) {
            Rectangle2D.Double bounds = new Rectangle2D.Double(x, y, w, h);
            return new GraphNode(label, bounds, bounds, fill, ShapeType.RECTANGLE, false);
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
