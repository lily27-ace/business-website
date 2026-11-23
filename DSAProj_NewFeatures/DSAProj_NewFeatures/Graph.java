import java.util.*;

public class Graph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, List<Edge>> adjList = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(String from, String to, double weight, String relation) {
        if (!nodes.containsKey(from) || !nodes.containsKey(to)) {
            throw new IllegalArgumentException("Both nodes must exist before adding edge: " + from + " -> " + to);
        }
        adjList.get(from).add(new Edge(to, weight, relation));
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public List<Edge> getNeighbors(String id) {
        return adjList.getOrDefault(id, Collections.emptyList());
    }

    public double getHeuristic(String id) {
        Node n = nodes.get(id);
        return (n != null) ? n.getHeuristic() : 0.0;
    }

    public boolean isScholarship(String id) {
        Node n = nodes.get(id);
        return n != null && n.getType() == Node.Type.SCHOLARSHIP;
    }

    // Helper: Checks if student is allowed to enter a node (prunes ineligible academic fields)
    public boolean isEligibleNode(String nodeId, Student student) {
        Node n = nodes.get(nodeId);
        if (n == null) return false;
        if (n.getType() == Node.Type.FIELD) {
            return student.getEligibleFieldIds().contains(nodeId);
        }
        return true; // Locations and Scholarships reached via valid paths are allowed
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    public int getVertexCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adjList.values()) {
            count += edges.size();
        }
        return count;
    }
}