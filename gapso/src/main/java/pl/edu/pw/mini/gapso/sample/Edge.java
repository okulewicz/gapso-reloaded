package pl.edu.pw.mini.gapso.sample;

public class Edge {
    private ScaledAssignedSample node1;
    private ScaledAssignedSample node2;
    private double distance;

    public Edge(ScaledAssignedSample node1, ScaledAssignedSample node2) {
        this.node1 = node1;
        this.node2 = node2;
        this.distance = node1.getDistance(node2);
    }

    public double getDistance() {
        return distance;
    }

    public ScaledAssignedSample getNode2() {
        return node2;
    }

    public ScaledAssignedSample getNode1() {
        return node1;
    }
}
