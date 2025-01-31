package cellsociety.Controller;

public class SimulationConfig {

    private String type;
    private String title;
    private String author;
    private String description;
    private int width;
    private int height;
    private int[] initialStates;
    private SimulationParameter parameter;

    public SimulationConfig(String type, String title, String author, String description,
                            int width, int height, int[] initialStates, SimulationParameter parameter) {
        this.type = type;
        this.title = title;
        this.author = author;
        this.description = description;
        this.width = width;
        this.height = height;
        this.initialStates = initialStates;
        this.parameter = parameter;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int[] getInitialStates() { return initialStates; }
    public SimulationParameter getParameter() { return parameter; }

}
