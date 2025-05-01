import java.util.Objects;

public class Connection {
    private Station fromStation;
    private Station toStation;
    private String lineColor;
    private double time;

    public Connection(final Station fromStation, final Station toStation, final String lineColor, final Double time) {  
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.lineColor = lineColor;
        this.time = time;
    }

    public Station getFromStation() {
        return fromStation;
    }

    public void setFromStation(Station fromStation) {
        this.fromStation = fromStation;
    }

    public Station getToStation() {
        return toStation;
    }

    public void setToStation(Station toStation) {
        this.toStation = toStation;
    }

    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {  // 参数名修正为小写
        this.lineColor = lineColor;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return fromStation.getName() + " -> " + toStation.getName() + " via " + lineColor + " (" + time + " mins)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return (fromStation.equals(that.fromStation) && toStation.equals(that.toStation)) ||
            (fromStation.equals(that.toStation) && toStation.equals(that.fromStation));
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromStation, toStation) + Objects.hash(toStation, fromStation);
    }


}