import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Station {

    private String name;
    private int x;
    private int y;
    private  List<Connection>connections;

    public Station(String name, int x, int y) {  // 删除无参构造函数
        this.name = name;
        this.x = x;
        this.y = y;
        this.connections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {  // 修正参数名
        this.connections = connections;
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }


// 方法重写
    @Override
    public String toString() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}