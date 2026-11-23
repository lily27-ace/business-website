import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Graph graph = DataLoader.buildScholarGraph();

        SwingUtilities.invokeLater(() -> {
            ScholarGraphGUI gui = new ScholarGraphGUI(graph);
            gui.setVisible(true);
        });
    }
}