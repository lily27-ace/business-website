import java.util.*;

public class AStar {
    private static class State {
        final String id;
        final double f;

        State(String id, double f) {
            this.id = id;
            this.f = f;
        }
    }

    public static BFS.SearchResult search(Graph graph, Student student) {
        long startTime = System.nanoTime();

        List<Node> results = new ArrayList<>();
        PriorityQueue<State> open = new PriorityQueue<>(Comparator.comparingDouble(s -> s.f));
        Map<String, Double> gScore = new HashMap<>();
        Set<String> visited = new HashSet<>();

        String start = student.getLocaleId();
        if (graph.getNode(start) == null) {
            return new BFS.SearchResult(results, System.nanoTime() - startTime, 0);
        }

        gScore.put(start, 0.0);
        open.add(new State(start, graph.getHeuristic(start)));

        while (!open.isEmpty()) {
            State current = open.poll();

            if (!visited.add(current.id)) continue;

            if (graph.isScholarship(current.id)) {
                results.add(graph.getNode(current.id));
            }

            for (Edge edge : graph.getNeighbors(current.id)) {
                String nextId = edge.getTo();
                if (!graph.isEligibleNode(nextId, student)) continue;

                double tentativeG = gScore.get(current.id) + edge.getWeight();
                if (tentativeG < gScore.getOrDefault(nextId, Double.MAX_VALUE)) {
                    gScore.put(nextId, tentativeG);
                    double f = tentativeG + graph.getHeuristic(nextId);
                    open.add(new State(nextId, f));
                }
            }
        }

        long elapsed = System.nanoTime() - startTime;
        return new BFS.SearchResult(results, elapsed, visited.size());
    }
}