public class Edge {
    private final String to;
    private final double weight; // Lower weight = closer proximity / higher priority fit
    private final String relation;

    public Edge(String to, double weight, String relation) {
        this.to = to;
        this.weight = weight;
        this.relation = relation;
    }

    public String getTo() { return to; }
    public double getWeight() { return weight; }
    public String getRelation() { return relation; }
}