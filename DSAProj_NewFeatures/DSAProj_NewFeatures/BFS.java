import java.util.*;

public class BFS {
    public static class SearchResult {
        public final List<Node> scholarships;
        public final long executionTimeNanos;
        public final int nodesVisited;

        public SearchResult(List<Node> scholarships, long executionTimeNanos, int nodesVisited) {
            this.scholarships = scholarships;
            this.executionTimeNanos = executionTimeNanos;
            this.nodesVisited = nodesVisited;
        }
    }

    public static SearchResult search(Graph graph, Student student) {
        long startTime = System.nanoTime();

        List<Node> results = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        String start = student.getLocaleId();
        if (graph.getNode(start) == null) {
            return new SearchResult(results, System.nanoTime() - startTime, 0);
        }

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();

            if (graph.isScholarship(currentId)) {
                results.add(graph.getNode(currentId));
            }

            for (Edge edge : graph.getNeighbors(currentId)) {
                String nextId = edge.getTo();
                if (!visited.contains(nextId) && graph.isEligibleNode(nextId, student)) {
                    visited.add(nextId);
                    queue.add(nextId);
                }
            }
        }

        long elapsed = System.nanoTime() - startTime;
        return new SearchResult(results, elapsed, visited.size());
    }
}