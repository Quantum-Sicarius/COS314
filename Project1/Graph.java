import java.util.ArrayList;

class Graph {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;

    public Graph() {
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();
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
                    edges.add(e);
                }
            }
        }
    }

    public void AddManualVertex(Edge e) {
        Vertex a = new Vertex(e.getVertexA().getName(), e.getVertexA().getCoordinates().getX(), e.getVertexA().getCoordinates().getY());
        Vertex b = new Vertex(e.getVertexB().getName(), e.getVertexB().getCoordinates().getX(), e.getVertexB().getCoordinates().getY());

        boolean containsA = false;
        for (Vertex v : this.vertices) {
            if(v.getName() == a.getName()) {
                containsA = true;
                a = v;
                break;
            }
        }
        if(!containsA) {
            this.vertices.add(a);
        }

        boolean containsB = false;
        for (Vertex v : this.vertices) {
            if(v.getName() == b.getName()) {
                containsB = true;
                b = v;
                break;
            }
        }
        if(!containsB) {
            this.vertices.add(b);
        }

        boolean edgeExisits = false;

        for (Edge origE : this.edges) {
            if(origE.getVertexA() == a && origE.getVertexB() == b) {
                // we already have such an edge
                System.out.println("WE ALREADY HAVE THIS EDGE!");
                edgeExisits = true;
                break;
            }
        }

        if(!edgeExisits) {
            Edge newEdge = new Edge(getDistance(a, b), a, b);
            this.edges.add(newEdge);
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

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public Vertex getStartingVertex() {
        Vertex start = this.vertices.get(0);
        for (Vertex v : this.vertices) {
            if(v.getName().trim().compareTo("1") == 0) {
                return v;
            }
        }

        return start;
    }
}