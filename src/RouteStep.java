import java.util.List;

public class RouteStep {
    private Station station;
    private List<Station> path;
    private int changes;
    private String currentLine;

    public RouteStep(Station station, List<Station> path, int changes, String currentLine) {
        this.station = station;
        this.path = path;
        this.changes = changes;
        this.currentLine = currentLine;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public List<Station> getPath() {
        return path;
    }

    public void setPath(List<Station> path) {
        this.path = path;
    
    }

    public int getChanges() {
        return changes;
    }

    public void setChanges(int changes) {
        this.changes = changes;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(String currentLine) {
        this.currentLine = currentLine;
    }


    @Override
    public String toString() {
        return "Step: " + station.getName() + ", Changes: " + changes + ", Line: " + currentLine;
    }
}
 
