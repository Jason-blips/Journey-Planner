import java.io.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;



public class GraphicMap extends JPanel {
    private BufferedImage mapImage;
    private java.util.List<Station> transferPoints;
    private java.util.List<Station> route;
    private NodeManager manager;
    private int renderedStepCount = Integer.MAX_VALUE;

    public GraphicMap() {
        loadMapImage();
    }

    public GraphicMap(NodeManager manager) {
        this.manager = manager;
        loadMapImage();
    }

    private void loadMapImage() {
        try {
            mapImage = ImageIO.read(new File("../data/map.jpg")); 
        } catch (IOException e) {
            System.err.println("Load the image failed.");
            e.printStackTrace();
        }
    }

    public void setRoute(java.util.List<Station> route) {
        this.route = route;
        this.transferPoints = getTransferPoints();
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
        // 1. 画背景图
        if (mapImage != null) {
            g2.drawImage(mapImage, 0, 0, this.getWidth(), this.getHeight(), null);
            
        }

        // 2. 画路径
        if (route != null && !route.isEmpty()) {
            g2.setStroke(new BasicStroke(3));

            int steps = Math.min(renderedStepCount, route.size() - 1);
            for (int i = 0; i < steps; i++) {
                Station curr = route.get(i);
                Station next = route.get(i + 1);
                String line = manager.getLine(curr, next);

                g2.setColor(getLineColor(line));
                g2.drawLine(curr.getX(), curr.getY(), next.getX(), next.getY());
            }

            // 3. 画站点
            for (Station s : route) {
                g2.setColor(Color.GRAY);
                int r = 10;
                g2.fillOval(s.getX() - r / 2, s.getY() - r / 2, r, r);

                g2.setColor(Color.BLACK);
                g2.drawString(s.getName(), s.getX() + 5, s.getY() - 5);
            }

            if (transferPoints != null) {
                for (Station t : transferPoints) {
                    g2.setColor(Color.MAGENTA);
                    int r = 14;
                    g2.fillOval(t.getX() - r / 2, t.getY() - r / 2, r, r);
    
                    g2.setColor(Color.WHITE);
                    g2.drawString("Exchange", t.getX() + 5, t.getY() + 15);

                }

            }

        }
    }


    private Color getLineColor(String lineName) {
        return switch (lineName.toLowerCase()) {
            case "yellow" -> Color.YELLOW;
            case "red" -> Color.RED;
            case "green" -> Color.GREEN;
            case "blue" -> Color.BLUE;
            case "pink" -> Color.PINK;
            case "purple" -> new Color(128, 0, 128);
            case "lightblue" -> new Color(173, 216, 230);
            default -> Color.BLACK;
        };
    }

    private java.util.List<Station> getTransferPoints() {
        
        java.util.List<Station> result = new java.util.ArrayList<>();

        if (route == null || route.size() < 3) {
            return result;
        }

        for (int i = 1; i < route.size() - 1; i++) {
            Station prev = route.get(i - 1);
            Station curr = route.get(i);
            Station next = route.get(i + 1);

            String lineBefore = manager.getLine(prev, curr);
            String lineAfter = manager.getLine(curr, next);

            if (lineBefore != null && lineAfter != null && !lineBefore.equals(lineAfter)) {
                result.add(curr);
            }
        }

        return result;
    }

    public void animatePath() {
        new Thread(() -> {
            for (int i = 1; i <= route.size(); i++) {
                renderedStepCount = i;
                repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
