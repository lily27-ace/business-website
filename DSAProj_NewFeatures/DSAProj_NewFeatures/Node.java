public class Node {
    public enum Type { LOCATION, FIELD, SCHOLARSHIP }

    private final String id;
    private final String name;
    private final Type type;
    private final double heuristic; // h(n) for A* search

    // Scholarship metadata for Details Modal & GWA eligibility filtering
    private String provider = "N/A";
    private String benefits = "N/A";
    private String deadline = "N/A";
    private String requirements = "N/A";
    private double minGwa = 0.0;

    public Node(String id, String name, Type type, double heuristic) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.heuristic = heuristic;
    }

    public void setScholarshipDetails(String provider, String benefits, String deadline, String requirements, double minGwa) {
        this.provider = provider;
        this.benefits = benefits;
        this.deadline = deadline;
        this.requirements = requirements;
        this.minGwa = minGwa;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Type getType() { return type; }
    public double getHeuristic() { return heuristic; }
    public String getProvider() { return provider; }
    public String getBenefits() { return benefits; }
    public String getDeadline() { return deadline; }
    public String getRequirements() { return requirements; }
    public double getMinGwa() { return minGwa; }

    @Override
    public String toString() {
        return name + " [" + type + "]";
    }
}