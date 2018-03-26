import java.util.ArrayList;

class Path {
    public ArrayList<Vertex> currentPath;
    public boolean complete = false;

    public Path() {
        currentPath = new ArrayList<Vertex>();
    }

    public Path(Path p) {
        currentPath = new ArrayList<Vertex>(p.currentPath);
    }

    public Vertex getLastAdded() {
        return currentPath.get(currentPath.size() - 1);
    }

    public void add(Vertex v) {
        this.currentPath.add(v);
    }

    public String toString() {
        String s = "";
        for (Vertex v : currentPath) {
            s += v.getName() + ",";
        }

        return s;
    }

    public double getTotalDistance() {
        double distance = 0.0;

        for (int i = 0; i < currentPath.size() - 1; i++) {
            distance += currentPath.get(i).getDistanceTo(currentPath.get(i+1));
        }

        return distance;
    }

    public void delete() {
        for (Vertex v : currentPath) {
            currentPath.remove(v);
        }
    }
}