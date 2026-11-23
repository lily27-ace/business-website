import java.util.*;

public class DFS {
    public static BFS.SearchResult search(Graph graph, Student student) {
        long startTime = System.nanoTime();

        List<Node> results = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        String start = student.getLocaleId();
        if (graph.getNode(start) == null) {
            return new BFS.SearchResult(results, System.nanoTime() - startTime, 0);
        }

        stack.push(start);

        while (!stack.isEmpty()) {
            String currentId = stack.pop();

            if (visited.contains(currentId)) continue;
            visited.add(currentId);

            if (graph.isScholarship(currentId)) {
                results.add(graph.getNode(currentId));
            }

            // Reverse order push so traversal follows natural edge declaration order
            List<Edge> neighbors = graph.getNeighbors(currentId);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                String nextId = neighbors.get(i).getTo();
                // Pruning: immediately drops branches whose academic field fails student eligibility
                if (!visited.contains(nextId) && graph.isEligibleNode(nextId, student)) {
                    stack.push(nextId);
                }
            }
        }

        long elapsed = System.nanoTime() - startTime;
        return new BFS.SearchResult(results, elapsed, visited.size());
    }
}