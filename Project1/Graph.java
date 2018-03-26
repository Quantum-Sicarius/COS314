import java.util.ArrayList;

class Graph {
    private ArrayList<Vertex> vertices;
	private int numOfEdges;

    public Graph() {
        this.vertices = new ArrayList<Vertex>();
    }

    // For each new vertex we need to add a new edge to all prexisting vertices.
    public void addNewVertex(String name, double x, double y) {
        Vertex newVertex = new Vertex(name, x, y);

        this.vertices.add(newVertex);

        // Add edges for each vertex.
        for (Vertex vA : this.vertices) {
            for (Vertex vB : this.vertices) {
                // If we are not the same vertex.
                // And we dont have an edge like this
                if(vA != vB && !vA.hasEdgeTo(vB)) {
                    Edge e = new Edge(getDistance(vA, vB), vA, vB);
                }
            }
        }
    }

    /*
    xd = x[i] - x[j];
    yd = y[i] - y[j];
    dij = nint( sqrt( xd*xd + yd*yd) )
    */
    private double getDistance(Vertex a, Vertex b) {
        double xd = a.getCoordinates().getX() - b.getCoordinates().getX();
        double yd = a.getCoordinates().getY() - b.getCoordinates().getY();

        return Math.sqrt((yd)*(yd) +(xd)*(xd));
    }

    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }
}