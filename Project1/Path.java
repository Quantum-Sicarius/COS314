import java.util.ArrayList;

class Path {
    public ArrayList<Vertex> currentPath;

    public Path() {
        currentPath = new ArrayList<Vertex>();
    }

    public void add(Vertex v) {
        this.currentPath.add(v);
    }

    public String toString() {
        String s = "";
        for (Vertex v : currentPath) {
            s += v + ",";
        }

        return s;
    }
}