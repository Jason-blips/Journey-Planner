import java.util.Scanner;
import java.util.List;

public class JourneyPlanner {
    public static void main(String[] args) throws Exception { 
        try(Scanner sc = new Scanner(System.in)) {
            NodeManager manager = new NodeManager();
            manager.loadFile("data/Metrolink_times_linecolour.csv"); 

            String startStation;
            String endStation;

            do {
                System.out.println("Enter start station: ");
                startStation = sc.nextLine();
            } while (!manager.isValidStation(startStation));

            do {
                System.out.println("Enter end station: ");
                endStation = sc.nextLine();
            } while (!manager.isValidStation(endStation));

            System.out.println("Start: " + startStation);
            System.out.println("End: " + endStation);

            List<Station> route = manager.findShortestTimeRoute(startStation, endStation);
            double totalTime = 0.0;
            if (route.isEmpty()) {
                System.out.println("Invalid route found!");

            } else {
                String currentLine = null;
                for (int i = 0; i < route.size(); i++) {
                    Station current = route.get(i);
                    if (i == 0) {
                        currentLine = manager.getLine(route.get(i), route.get(i + 1));
                        System.out.println(current.getName() + " on " + currentLine + " line");

                    } else if (i < route.size() - 1) {
                        String nextLine = manager.getLine(current, route.get(i + 1));
                        if (!nextLine.equals(currentLine)) {
                            System.out.println("** Change Line to " + nextLine + " ***");
                            currentLine = nextLine;
                            totalTime += 2.0; // 累加换乘时间
                        }

                    }

                    double travelTime = manager.getTravelTime(current, route.get(i + 1));
                    System.out.println(current.getName() + " on " + currentLine + " line");
                    totalTime += travelTime;
                }
            }
            System.out.println("Overall Journey Time: " + totalTime + " mins");
        }
    }    
}

