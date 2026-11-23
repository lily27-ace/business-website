import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DataLoader {
    private static final String CSV_FILE = "scholarships.csv";

    public static Graph buildScholarGraph() {
        ensureCsvExists();
        Graph graph = new Graph();

        // 1. LOCATION NODES
        graph.addNode(new Node("ILOILO_CITY", "Iloilo City", Node.Type.LOCATION, 1.0));
        graph.addNode(new Node("ROXAS_CITY", "Roxas City", Node.Type.LOCATION, 1.0));
        graph.addNode(new Node("CAPIZ", "Province of Capiz", Node.Type.LOCATION, 1.5));
        graph.addNode(new Node("WESTERN_VISAYAS", "Western Visayas (Region VI)", Node.Type.LOCATION, 2.0));
        graph.addNode(new Node("PHILIPPINES", "Philippines (National)", Node.Type.LOCATION, 2.0));

        // 2. FIELD OF STUDY NODES
        graph.addNode(new Node("STEM", "STEM Track", Node.Type.FIELD, 1.0));
        graph.addNode(new Node("IT", "Information Technology / ICT", Node.Type.FIELD, 1.0));
        graph.addNode(new Node("CHED_PRIORITY", "CHED Priority Courses", Node.Type.FIELD, 1.0));
        graph.addNode(new Node("ALL_FIELDS", "All Academic Fields", Node.Type.FIELD, 1.0));

        // 3. PARSE & VALIDATE SCHOLARSHIP NODES FROM CSV
        loadScholarshipsFromCsv(graph);

        // 4. DIRECTED WEIGHTED EDGES
        graph.addEdge("ILOILO_CITY", "WESTERN_VISAYAS", 1.5, "Located in Region");
        graph.addEdge("ROXAS_CITY", "CAPIZ", 1.0, "Located in Province");
        graph.addEdge("CAPIZ", "WESTERN_VISAYAS", 1.5, "Located in Region");
        graph.addEdge("WESTERN_VISAYAS", "PHILIPPINES", 2.0, "Part of National");

        graph.addEdge("ILOILO_CITY", "SCH_ILOILO_GOV", 1.0, "City Residency Eligible");
        graph.addEdge("ROXAS_CITY", "SCH_ROXAS_GOV", 1.0, "City Residency Eligible");
        graph.addEdge("CAPIZ", "SCH_CAPIZ_PROV", 1.2, "Provincial Residency Eligible");

        graph.addEdge("WESTERN_VISAYAS", "CHED_PRIORITY", 1.5, "Regional Priority Track");
        graph.addEdge("WESTERN_VISAYAS", "STEM", 1.5, "Regional S&T Track");
        graph.addEdge("WESTERN_VISAYAS", "SCH_DOST_R6", 2.0, "Regional S&T Quota");

        graph.addEdge("PHILIPPINES", "STEM", 2.0, "National STEM Track");
        graph.addEdge("PHILIPPINES", "IT", 2.0, "National ICT Track");
        graph.addEdge("PHILIPPINES", "ALL_FIELDS", 2.5, "Open Track");

        graph.addEdge("CHED_PRIORITY", "SCH_CHED_R6", 1.0, "Course Prerequisite");
        graph.addEdge("STEM", "SCH_DOST_MERIT", 1.0, "Course Prerequisite");
        graph.addEdge("IT", "SCH_DICT_ICT", 1.0, "Course Prerequisite");
        graph.addEdge("IT", "SCH_SM_FOUNDATION", 1.2, "Partner Course");
        graph.addEdge("STEM", "SCH_SM_FOUNDATION", 1.2, "Partner Course");
        graph.addEdge("ALL_FIELDS", "SCH_CHED_R6", 1.8, "General Eligibility");

        return graph;
    }

    private static void ensureCsvExists() {
        Path path = Paths.get(CSV_FILE);
        if (Files.exists(path)) return;

        List<String> lines = Arrays.asList(
            "id|name|provider|benefits|deadline|minGwa|requirements",
            "SCH_ILOILO_GOV|Iloilo City Government Scholarship|Iloilo City LGU|Full Tuition + PHP 3,000 Monthly Allowance|June 30, 2026|85.0|Bona fide Iloilo City resident; Indigency certificate; Min GWA 85%",
            "SCH_ROXAS_GOV|Roxas City Educational Assistance|Roxas City LGU|PHP 5,000 Semestral Financial Grant|July 15, 2026|82.0|Registered Roxas City resident; Certificate of Enrollment; Min GWA 82%",
            "SCH_CAPIZ_PROV|Provincial Government of Capiz Scholarship|Capiz Provincial Gov|PHP 10,000 Per Semester + Book Stipend|July 31, 2026|85.0|Capiz resident for at least 2 years; Good moral character; Min GWA 85%",
            "SCH_CHED_R6|CHED Region VI Tulong Dunong / Merit Grant|CHED Regional Office VI|PHP 15,000 to PHP 30,000 Annual Grant|August 15, 2026|85.0|Enrolled in CHED-recognized HEI in Region VI; Min GWA 85%",
            "SCH_DOST_R6|DOST-SEI Region VI Core S&T Track|DOST-SEI Region VI|Tuition + PHP 8,000/mo Stipend + Book Allowance|November 22, 2026|88.0|Pass DOST-SEI Qualifying Exam; Region VI S&T priority course; Min GWA 88%",
            "SCH_DOST_MERIT|DOST-SEI National Merit Scholarship|DOST-SEI National|Full Tuition + PHP 8,000/mo Stipend + Thesis Grant|November 22, 2026|90.0|Top 5% of graduating STEM class; Pass DOST-SEI Exam; Min GWA 90%",
            "SCH_DICT_ICT|DICT ICT Specialized Scholarship|Dept. of ICT (DICT)|Full Tuition + Certification Vouchers + Laptop Grant|September 10, 2026|87.0|Enrolled in IT/CS/IS degree; Coding aptitude screening; Min GWA 87%",
            "SCH_SM_FOUNDATION|SM Foundation College Scholarship|SM Foundation Inc.|100% Tuition + Monthly Allowance + Part-Time Jobs|March 31, 2026|88.0|Public high school graduate; Household income <= PHP 250k/yr; Min GWA 88%"
        );

        try {
            Files.write(path, lines);
        } catch (IOException e) {
            System.err.println("Warning: Could not write default CSV file: " + e.getMessage());
        }
    }

    private static void loadScholarshipsFromCsv(Graph graph) {
        Set<String> seenIds = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                // Data Validator: Ensure 7 columns & no duplicate IDs
                if (parts.length < 7) continue;

                String id = parts[0].trim();
                if (seenIds.contains(id)) continue;
                seenIds.add(id);

                String name = parts[1].trim();
                String provider = parts[2].trim();
                String benefits = parts[3].trim();
                String deadline = parts[4].trim();
                double minGwa = Double.parseDouble(parts[5].trim());
                String requirements = parts[6].trim();

                Node schNode = new Node(id, name, Node.Type.SCHOLARSHIP, 0.0);
                schNode.setScholarshipDetails(provider, benefits, deadline, requirements, minGwa);
                graph.addNode(schNode);
            }
        } catch (Exception e) {
            System.err.println("CSV Read Error: " + e.getMessage());
        }
    }

    public static Set<String> expandFields(String primaryField) {
        Set<String> fields = new HashSet<>();
        fields.add("ALL_FIELDS");
        switch (primaryField.toUpperCase()) {
            case "IT":
                fields.add("IT");
                fields.add("STEM");
                fields.add("CHED_PRIORITY");
                break;
            case "STEM":
                fields.add("STEM");
                fields.add("CHED_PRIORITY");
                break;
            case "CHED_PRIORITY":
                fields.add("CHED_PRIORITY");
                break;
            default:
                fields.add(primaryField.toUpperCase());
                break;
        }
        return fields;
    }
}