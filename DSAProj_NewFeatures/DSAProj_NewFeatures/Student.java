import java.util.Set;

public class Student {
    private final String name;
    private final String localeId;
    private final Set<String> eligibleFieldIds;
    private final double gwa; // General Weighted Average (e.g., 85.0 - 100.0)

    public Student(String name, String localeId, Set<String> eligibleFieldIds) {
        this(name, localeId, eligibleFieldIds, 95.0);
    }

    public Student(String name, String localeId, Set<String> eligibleFieldIds, double gwa) {
        this.name = name;
        this.localeId = localeId;
        this.eligibleFieldIds = eligibleFieldIds;
        this.gwa = gwa;
    }

    public String getName() { return name; }
    public String getLocaleId() { return localeId; }
    public Set<String> getEligibleFieldIds() { return eligibleFieldIds; }
    public double getGwa() { return gwa; }
}