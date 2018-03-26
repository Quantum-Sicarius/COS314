import java.util.ArrayList;
import java.awt.Point;

class Vertex {
    private String name;
    private ArrayList<Edge> edges;
    private Point coordinates;

    public double currentDistanceFromStart = Double.POSITIVE_INFINITY;
    public Vertex previousVertex = null;

    public Vertex(String name, double x, double y) {
        this.name = name;
        this.edges = new ArrayList<Edge>();
        this.coordinates = new Point();
        this.coordinates.setLocation(x, y);
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void addEdge(Edge e) {
        this.edges.add(e);
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasPreviousVertex() {
        if(previousVertex != null) {
            return true;
        }
        return false;
    }

    public double getDistanceTo(Vertex v) {
        double xd = this.getCoordinates().getX() - v.getCoordinates().getX();
        double yd = this.getCoordinates().getY() - v.getCoordinates().getY();

        return Math.sqrt((yd)*(yd) +(xd)*(xd));
    }

    public boolean hasEdgeTo(Vertex b) {
        for (Edge e : this.edges) {
            if(e.getVertexA() == this && e.getVertexB() == b) {
                return true;
            }
            if(e.getVertexA() == b && e.getVertexB() == this) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<Vertex> getNeighbors() {
        ArrayList<Vertex> neighbors = new ArrayList<Vertex>();
        for (Edge e : this.edges) {
            if(e.getVertexA() != this) {
                neighbors.add(e.getVertexA());
            }
            if(e.getVertexB() != this) {
                neighbors.add(e.getVertexB());
            }
        }

        return neighbors;
    }
}