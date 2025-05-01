
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class JourneyPlannerGUI extends JFrame {

    private GraphicMap graphicMap; 
    private JComboBox<String> startBox;
    private JComboBox<String> endBox;
    private JTextArea outputArea;
    private JRadioButton shortestTimeButton;
    private JRadioButton leastChangesButton;
    private NodeManager manager;

    public JourneyPlannerGUI() {
        setTitle("Manchester Metrolink Journey Planner");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        manager = new NodeManager();
        try {
            manager.loadFile("../data/Metrolink_times_linecolour.csv");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load station data: " + e.getMessage());
            return;
        }

        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        startBox = new JComboBox<>(manager.getNodeMap().keySet().toArray(new String[0]));
        endBox = new JComboBox<>(manager.getNodeMap().keySet().toArray(new String[0]));

        topPanel.add(new JLabel("From:"));
        topPanel.add(startBox);
        topPanel.add(new JLabel("To:"));
        topPanel.add(endBox);

        shortestTimeButton = new JRadioButton("Shortest Time", true);
        leastChangesButton = new JRadioButton("Least Transfers");
        ButtonGroup group = new ButtonGroup();
        group.add(shortestTimeButton);
        group.add(leastChangesButton);

        topPanel.add(shortestTimeButton);
        topPanel.add(leastChangesButton);

        JButton planButton = new JButton("Plan Journey");
        planButton.addActionListener(this::handlePlanJourney);
        topPanel.add(planButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> outputArea.setText(""));
        topPanel.add(clearButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        graphicMap = new GraphicMap(manager);
        add(graphicMap, BorderLayout.SOUTH);
        graphicMap.setPreferredSize(new Dimension(800,600));

    }

    private void handlePlanJourney(ActionEvent event) {
        String from = (String) startBox.getSelectedItem();
        String to = (String) endBox.getSelectedItem();

        if (from.equals(to)) {
            showError("Start and end stations must be different.");
            return;
        }

        outputArea.setText("");  // clear previous result
        List<Station> route;

        boolean showTime = shortestTimeButton.isSelected();
        route = showTime
                ? manager.findShortestTimeRoute(from, to) : manager.findLeastChangeRoute(from, to);

        if (route.isEmpty()) {
            outputArea.setText("No valid route found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("*** Route from ").append(from).append(" to ").append(to).append(" ***");
        
        double totalTime = 0.0;
        int totalChanges = 0;
        String currentLine = manager.getLine(route.get(0), route.get(1));

        for (int i = 0; i < route.size() - 1; i++) {
            Station curr = route.get(i);
            Station next = route.get(i + 1);
            String nextLine = manager.getLine(curr, next);
            Double travelTime = manager.getTravelTime(curr, next);
            
            if (!nextLine.equals(currentLine)) {
                sb.append("** Change Line to ").append(nextLine).append(" **\n");
                if (showTime) totalTime += 2.0;
                totalChanges++;
            }    
            
            sb.append(curr.getName()).append(" on ").append(nextLine).append(" line (").append(String.format("%.1f", travelTime)).append(" mins)\n");
            
            if (showTime) totalTime += travelTime;
            currentLine = nextLine;
        }
        sb.append(route.get(route.size() - 1).getName()).append(" on ").append(currentLine).append(" line\n");

        if (showTime) {
            sb.append("\nTotal Journey Time: ").append(String.format("%.1f", totalTime)).append(" mins\n");
        }
        sb.append("Total Changes: ").append(totalChanges).append("\n");
        
        outputArea.setText(sb.toString());
        graphicMap.setRoute(route);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JourneyPlannerGUI().setVisible(true));
        System.out.println("Working dir: " + System.getProperty("user.dir"));

    }

}
