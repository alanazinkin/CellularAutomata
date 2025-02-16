package cellsociety.controller;

import java.util.ResourceBundle;

public record SimulationParameters(
        double fireProb,
        double treeProb,
        double satisfaction,
        double percolationProb
) {
    private static final ResourceBundle CONFIG = ResourceBundle.getBundle(
            SimulationParameters.class.getPackageName() + ".Simulation");

    public static SimulationParameters fromConfig() {
        return new SimulationParameters(
                Double.parseDouble(CONFIG.getString("default.fire.prob")),
                Double.parseDouble(CONFIG.getString("default.tree.prob")),
                Double.parseDouble(CONFIG.getString("default.satisfaction")),
                Double.parseDouble(CONFIG.getString("default.percolation.prob"))
        );
    }
}
