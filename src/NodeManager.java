import java.util.*;
import java.io.*;

public class NodeManager {
    private Map<String, Station> nodeMap = new HashMap<>(); // 封装为 private

    public void loadFile(String filename) throws IOException { 
        File f = new File(filename);
        System.out.println("Trying to load: " + f.getAbsolutePath());   // Absolute Path: /Users/alice/Documents/JavaProjects/MetroApp/data/Metrolinked_times_planner.csv
                                                                        // Relative Path: ../data/Metrolinked_times_planner.csv
        System.out.println("File exists? " + f.exists());

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {    // Open the file named filename for reading.

                                                                                    // BufferedReader helps read the file line by line.
            
                                                                                    // try (...) ensures the file is closed automatically after reading.
            
            String line;                                                            // Declare a variable line to store each line of the file.

            br.readLine(); // Skip the header                                       // Read and ignore the first line (the header of the CSV file).

            while ((line = br.readLine()) != null) {                                // Read each line of the file one by one until the end (null means no more lines).

                                                                                    // Store the line in the line variable.


                String[] parts = line.split(",");                             // Split the line by commas , and store each part in an array called parts.

                if (parts.length != 4) {                                            // Check if the line has exactly 4 items (as expected for a valid line).

                    System.out.println("Invalid line format: " + line);             // If the line doesn't have 4 items, print a warning showing the bad line.

                    continue;                                                       // Skip this line and go to the next one in the loop.
                }

                for (String value : parts) {                                        // This is a "for-each" loop that goes through each element in the parts array.

                    System.out.print(value + " | ");                                // Print the value followed by " | " without moving to a new line.

                }

                System.out.println();                                               // Print a newline (go to the next line).

                String from = parts[0];
                String to = parts[1];
                String lineColor = parts[2];
                double time;
                try {
                    time = Double.parseDouble(parts[3]); // 处理数值解析异常
                } catch (NumberFormatException e) {
                    System.out.println("Invalid time format: " + line);
                    continue;
                }

                Station fromStation = nodeMap.computeIfAbsent(from, name -> new Station(name, 0, 0)); 
                Station toStation = nodeMap.computeIfAbsent(to, name -> new Station(name, 0, 0));


                fromStation.addConnection(new Connection(fromStation, toStation, lineColor, time));
                toStation.addConnection(new Connection(toStation, fromStation, lineColor, time));

                printConnection(from, to, lineColor, time);
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + e.getMessage());
        }
    }

    private void printConnection(String from, String to, String lineColor, double time) {
        System.out.printf("%s -> %s via %s line takes %.1f mins\n", from, to, lineColor, time);
    }

    public List<Station> findAnyRoute(String startStation, String endStation) {
        throw new UnsupportedOperationException("Route finding not implemented");
    }

    public boolean isValidStation(String stationName) {
        return nodeMap.containsKey(stationName); // 补充缺失的方法
    }

    public Map<String, Station> getNodeMap() { // 提供 getter
        return nodeMap;
    }

    public List<String> getStationNames() {
        return new ArrayList<>(nodeMap.keySet());
    }

    /**
     * Calculates the shortest time route from the start station to the end station.
     * Uses Dijkstra's algorithm with adjustments for transfer time between
     * different lines.
     * 
     * @param startStationName the starting station's name
     * @param endStationName   the destination station's name
     * @return the list of stations representing the shortest route
     */
    public List<Station> findShortestTimeRoute(String startStationName, String endStationName) {
        Station startStation = nodeMap.get(startStationName);
        Station endStation = nodeMap.get(endStationName);
        if (startStation == null || endStation == null) {
            System.out.println("Invalid station name(s): " + startStationName + ", " + endStationName);
            return Collections.emptyList();
        }

        // 初始化距离
        Map<Station, Double> distance = new HashMap<>();
        Map<Station, Station> prev = new HashMap<>();
        Map<Station, Boolean> visited = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>(Comparator.comparingDouble(distance::get));

        nodeMap.values().forEach(s -> {
            distance.put(s, Double.MAX_VALUE);
            prev.put(s, null);
            visited.put(s, false);
        });
        distance.put(startStation, 0.0);
        queue.add(startStation);

        while (!queue.isEmpty()) {
            Station current = queue.poll();
            if (visited.get(current))
                continue;
            visited.put(current, true);
            if (current.equals(endStation))
                break;

            for (Connection conn : current.getConnections()) {
                Station neighbor = conn.getToStation();
                double newDist = distance.get(current) + conn.getTime();

                // 换乘检验
                if (prev.get(current) != null) {
                    Connection prevConn = findConnection(prev.get(current), current);
                    if (prevConn != null && !prevConn.getLineColor().equals(conn.getLineColor())) {
                        newDist += 2.0;
                    }
                }

                if (newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    // 优先更新队列
                    if (queue.contains(neighbor)) {
                        queue.remove(neighbor);
                    }
                    queue.add(neighbor);
                }
            }
        }
        return buildPath(prev, endStation);
    }

    private List<Station> buildPath(Map<Station, Station> prev, Station endStation) {
        LinkedList<Station> path = new LinkedList<>();
        Station current = endStation;
        while (current != null) {
            path.addFirst(current);
            current = prev.get(current);
        }
        return path;
    }

    private Connection findConnection(Station fromStation, Station toStation) {
        return fromStation.getConnections().stream()
                .filter(c -> c.getToStation().equals(toStation))
                .findFirst()
                .orElse(null);
    }

    private class RouteStep {
        Station station;
        List<Station> path;
        int changes;
        String currentLine;

        RouteStep(Station station, List<Station> path, int changes, String currentLine) {
            this.station = station;
            this.path = path;
            this.changes = changes;
            this.currentLine = currentLine;
        }
    }

    public String getLine(Station fromStation, Station toStation) {
        for (Connection conn : fromStation.getConnections()) {
            if (conn.getToStation().equals(toStation)) {
                return conn.getLineColor();
            }
        }
        return "Unknown";
    }

    public double getTravelTime(Station from, Station to) {
        for (Connection conn : from.getConnections()) {
            if (conn.getToStation().equals(to)) {
                return conn.getTime();
            }
        }
        return Double.MAX_VALUE;
    }

    public void clear() {
        nodeMap.clear();
    }

    public List<Station> findLeastChangeRoute(String startStationName, String endStationName) {
        Station start = nodeMap.get(startStationName);
        Station end = nodeMap.get(endStationName);

        if (start == null || end == null)
            return Collections.emptyList();

        Queue<RouteStep> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // 初始化 BFS
        for (Connection conn : start.getConnections()) {
            List<Station> initialPath = new ArrayList<>();
            initialPath.add(start);
            initialPath.add(conn.getToStation());
            queue.add(new RouteStep(conn.getToStation(), initialPath, 0, conn.getLineColor()));
        }

        while (!queue.isEmpty()) {
            RouteStep current = queue.poll();
            Station currStation = current.station;

            String visitKey = currStation.getName() + "_" + current.currentLine;
            if (visited.contains(visitKey))
                continue;
            visited.add(visitKey);

            if (currStation.equals(end)) {
                return current.path;
            }

            for (Connection conn : currStation.getConnections()) {
                Station neighbor = conn.getToStation();
                if (current.path.contains(neighbor))
                    continue; // 避免回环

                int newChanges = current.changes;
                if (!conn.getLineColor().equals(current.currentLine)) {
                    newChanges++; // 换线
                }

                List<Station> newPath = new ArrayList<>(current.path);
                newPath.add(neighbor);
                queue.add(new RouteStep(neighbor, newPath, newChanges, conn.getLineColor()));
            }
        }

        return Collections.emptyList(); // 找不到路径
    }

    

}
