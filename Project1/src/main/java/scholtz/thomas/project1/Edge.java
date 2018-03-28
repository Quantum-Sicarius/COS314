package scholtz.thomas.project1;

import java.util.ArrayList;

class Edge {
    private Vertex vertexA;
    private Vertex vertexB;
    private double distance;

    public Edge(double distance, Vertex vertexA, Vertex vertexB) {
        this.distance = distance;
        this.vertexA = vertexA;
        this.vertexB = vertexB;

        this.vertexA.addEdge(this);
        this.vertexB.addEdge(this);
    }

    public Vertex getVertexA() {
        return this.vertexA;
    }

    public Vertex getVertexB() {
        return this.vertexB;
    }

    public double getDistance() {
        return this.distance;
    }
}