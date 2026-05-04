import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Router {
    String name;
    int x, y;
    java.util.List<Router> neighbors = new ArrayList<>();
    boolean hasReceiver = false;
    boolean pruned = false;

    public Router(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public void connect(Router r) {
        neighbors.add(r);
    }
}

class NetworkPanel extends JPanel {

    java.util.List<Router> routers;
    int packetX = -1, packetY = -1;

    public NetworkPanel(java.util.List<Router> routers) {
        this.routers = routers;
    }

    public void setPacket(int x, int y) {
        packetX = x;
        packetY = y;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw edges
        g.setColor(Color.BLACK);
        for (Router r : routers) {
            for (Router n : r.neighbors) {
                g.drawLine(r.x, r.y, n.x, n.y);
            }
        }

        // Draw nodes
        for (Router r : routers) {
            if (r.pruned) g.setColor(Color.RED);
            else if (r.hasReceiver) g.setColor(Color.GREEN);
            else g.setColor(Color.LIGHT_GRAY);

            g.fillOval(r.x - 15, r.y - 15, 30, 30);
            g.setColor(Color.BLACK);
            g.drawString(r.name, r.x - 5, r.y + 5);
        }

        // Draw packet
        if (packetX != -1) {
            g.setColor(Color.BLUE);
            g.fillOval(packetX - 5, packetY - 5, 10, 10);
        }
    }
}

public class PIMDMGUI {

    static JTextArea logArea;

    // 🔹 Logging
    static void log(String msg) {
        logArea.append(msg + "\n");
    }

    // 🔹 Animation
    static void movePacket(NetworkPanel panel, Router from, Router to) throws InterruptedException {
        int steps = 20;
        for (int i = 0; i <= steps; i++) {
            int x = from.x + (to.x - from.x) * i / steps;
            int y = from.y + (to.y - from.y) * i / steps;
            panel.setPacket(x, y);
            Thread.sleep(40);
        }
    }

    // 🔹 Flood
    static void flood(NetworkPanel panel, Router node, Set<Router> visited) throws InterruptedException {
        visited.add(node);
        log("Flood → " + node.name);

        for (Router n : node.neighbors) {
            if (!visited.contains(n) && !n.pruned) {
                movePacket(panel, node, n);
                flood(panel, n, visited);
            }
        }
    }

    // 🔹 Prune
    static boolean prune(Router node, Router parent) {
        boolean hasReceiver = node.hasReceiver;

        for (Router n : node.neighbors) {
            if (n != parent) {
                hasReceiver |= prune(n, node);
            }
        }

        if (!hasReceiver) {
            node.pruned = true;
            log("Pruned → " + node.name);
        }

        return hasReceiver;
    }

    // 🔹 Graft
    static void graft(Router node, Router parent) {
        if (node.pruned && node.hasReceiver) {
            node.pruned = false;
            log("Graft → " + node.name);
        }

        for (Router n : node.neighbors) {
            if (n != parent) {
                graft(n, node);
            }
        }
    }

    public static void main(String[] args) {

        // 🔹 Topology
        Router A = new Router("A", 300, 50);
        Router B = new Router("B", 200, 150);
        Router C = new Router("C", 400, 150);
        Router D = new Router("D", 120, 250);
        Router E = new Router("E", 280, 250);
        Router F = new Router("F", 360, 250);
        Router G = new Router("G", 520, 250);

        A.connect(B); B.connect(A);
        A.connect(C); C.connect(A);
        B.connect(D); D.connect(B);
        B.connect(E); E.connect(B);
        C.connect(F); F.connect(C);
        C.connect(G); G.connect(C);

        java.util.List<Router> routers = Arrays.asList(A,B,C,D,E,F,G);

        // Receivers
        D.hasReceiver = true;
        F.hasReceiver = true;

        // 🔹 GUI
        JFrame frame = new JFrame("PIM-DM Simulator");
        frame.setSize(900, 500);
        frame.setLayout(new BorderLayout());

        NetworkPanel panel = new NetworkPanel(routers);

        // Buttons
        JPanel control = new JPanel();

        JButton floodBtn = new JButton("Flood");
        JButton pruneBtn = new JButton("Prune");
        JButton graftBtn = new JButton("Graft");

        control.add(floodBtn);
        control.add(pruneBtn);
        control.add(graftBtn);

        // Log panel
        logArea = new JTextArea(10, 30);
        JScrollPane scroll = new JScrollPane(logArea);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(control, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // 🔹 Button Actions

        floodBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    log("\n=== FLOOD START ===");
                    flood(panel, A, new HashSet<>());
                } catch (Exception ex) {}
            }).start();
        });

        pruneBtn.addActionListener(e -> {
            log("\n=== PRUNE START ===");
            prune(A, null);
            panel.repaint();
        });

        graftBtn.addActionListener(e -> {
            log("\n=== GRAFT START ===");

            // simulate new receiver
            G.hasReceiver = true;

            graft(A, null);
            panel.repaint();
        });
    }
}