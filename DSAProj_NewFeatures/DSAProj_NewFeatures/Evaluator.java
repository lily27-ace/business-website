import java.util.*;

public class Evaluator {

    public static void runFullEvaluationAndBenchmark(Graph graph) {
        System.out.println("\n======================================================================");
        System.out.println("  SCHOLARGRAPH EVALUATION: CONFUSION MATRIX, METRICS & BENCHMARKS");
        System.out.println("======================================================================");

        // Test Profiles with Manually Labeled Ground Truth
        List<Student> testProfiles = Arrays.asList(
            new Student("Profile 1 (Iloilo City, IT)", "ILOILO_CITY", DataLoader.expandFields("IT")),
            new Student("Profile 2 (Roxas City, STEM)", "ROXAS_CITY", DataLoader.expandFields("STEM")),
            new Student("Profile 3 (Iloilo City, Non-STEM/General)", "ILOILO_CITY", DataLoader.expandFields("ALL_FIELDS"))
        );

        Map<String, Set<String>> groundTruth = new HashMap<>();
        groundTruth.put("Profile 1 (Iloilo City, IT)", new HashSet<>(Arrays.asList(
            "SCH_ILOILO_GOV", "SCH_CHED_R6", "SCH_DOST_R6", "SCH_DOST_MERIT", "SCH_DICT_ICT", "SCH_SM_FOUNDATION"
        )));
        groundTruth.put("Profile 2 (Roxas City, STEM)", new HashSet<>(Arrays.asList(
            "SCH_ROXAS_GOV", "SCH_CAPIZ_PROV", "SCH_CHED_R6", "SCH_DOST_R6", "SCH_DOST_MERIT", "SCH_SM_FOUNDATION"
        )));
        groundTruth.put("Profile 3 (Iloilo City, Non-STEM/General)", new HashSet<>(Arrays.asList(
            "SCH_ILOILO_GOV", "SCH_CHED_R6"
        )));

        // Collect all scholarship IDs for TN calculation
        Set<String> allScholarships = new HashSet<>();
        for (Node n : graph.getAllNodes()) {
            if (n.getType() == Node.Type.SCHOLARSHIP) {
                allScholarships.add(n.getId());
            }
        }

        int tp = 0, fp = 0, fn = 0, tn = 0;
        long totalBfsTime = 0, totalDfsTime = 0, totalAStarTime = 0;

        // Warm-up JVM slightly for cleaner nanoTime readings
        AStar.search(graph, testProfiles.get(0));

        for (Student s : testProfiles) {
            BFS.SearchResult bfsRes = BFS.search(graph, s);
            BFS.SearchResult dfsRes = DFS.search(graph, s);
            BFS.SearchResult astarRes = AStar.search(graph, s);

            totalBfsTime += bfsRes.executionTimeNanos;
            totalDfsTime += dfsRes.executionTimeNanos;
            totalAStarTime += astarRes.executionTimeNanos;

            Set<String> predicted = new HashSet<>();
            for (Node n : astarRes.scholarships) {
                predicted.add(n.getId());
            }
            Set<String> actual = groundTruth.get(s.getName());

            for (String schId : allScholarships) {
                boolean isPred = predicted.contains(schId);
                boolean isAct = actual.contains(schId);
                if (isPred && isAct) tp++;
                else if (isPred && !isAct) fp++;
                else if (!isPred && isAct) fn++;
                else tn++;
            }

            System.out.println("\n--- " + s.getName() + " ---");
            System.out.println("  BFS Order (Closest Layer): " + formatNames(bfsRes.scholarships));
            System.out.println("  DFS Order (Path/Pruned)  : " + formatNames(dfsRes.scholarships));
            System.out.println("  A*  Order (Ranked f(n))  : " + formatNames(astarRes.scholarships));
        }

        double precision = (tp + fp) == 0 ? 0 : (double) tp / (tp + fp);
        double recall = (tp + fn) == 0 ? 0 : (double) tp / (tp + fn);
        double f1 = (precision + recall) == 0 ? 0 : 2 * (precision * recall) / (precision + recall);

        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("1. CONFUSION MATRIX & CLASSIFICATION METRICS (Across All Test Profiles)");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("  True Positives (TP) : %d   | False Positives (FP): %d%n", tp, fp);
        System.out.printf("  False Negatives (FN): %d   | True Negatives (TN) : %d%n", fn, tn);
        System.out.printf("  Precision           : %.2f%%%n", precision * 100);
        System.out.printf("  Recall              : %.2f%%%n", recall * 100);
        System.out.printf("  F1-Score            : %.2f%%%n", f1 * 100);

        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("2. ALGORITHM BENCHMARK COMPARISON (Graph V=" + graph.getVertexCount() + ", E=" + graph.getEdgeCount() + ")");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-15s | %-18s | %-15s | %-20s%n", "Algorithm", "Avg Time (ns)", "Time (ms)", "Ranking Behavior");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-15s | %-18d | %-15.4f | %-20s%n", "BFS", totalBfsTime / 3, (totalBfsTime / 3) / 1_000_000.0, "Closest Layer First");
        System.out.printf("%-15s | %-18d | %-15.4f | %-20s%n", "DFS", totalDfsTime / 3, (totalDfsTime / 3) / 1_000_000.0, "Deep Path + Pruning");
        System.out.printf("%-15s | %-18d | %-15.4f | %-20s%n", "A* Search", totalAStarTime / 3, (totalAStarTime / 3) / 1_000_000.0, "Weighted Optimal f(n)");
        System.out.println("======================================================================\n");
    }

    private static List<String> formatNames(List<Node> nodes) {
        List<String> names = new ArrayList<>();
        for (Node n : nodes) {
            names.add(n.getName());
        }
        return names;
    }
}