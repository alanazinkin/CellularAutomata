package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.simulations.Fire;
import cellsociety.model.simulations.GameOfLife;
import cellsociety.model.simulations.LangtonLoop;
import cellsociety.model.simulations.Percolation;
import cellsociety.model.simulations.Sand;
import cellsociety.model.simulations.Schelling;
import cellsociety.model.simulations.SugarScape;
import cellsociety.model.simulations.WaTorWorld;
import java.util.Map;

public class SimulationFactory {
    public static Simulation createSimulation(String type, SimulationConfig config,
                                              Grid grid) {
        Map<String, Double> parameters = config.getParameters();
        return SimulationController.SimulationType.fromString(type)
                .map(simType -> switch (simType) {
                    case GAME_OF_LIFE -> new GameOfLife(config, grid);
                    case SPREADING_FIRE ->
                            new Fire(config, grid, parameters.get("fireProb"), parameters.get("treeProb"));
                    case PERCOLATION ->
                            new Percolation(config, grid, parameters.get("percolationProb"));
                    case SCHELLING ->
                            new Schelling(config, grid, parameters.get("satisfaction"));
                    case WATOR_WORLD ->
                        new WaTorWorld(config, grid, parameters.get("fishBreedTime"), parameters.get("sharkBreedTime"),
                            parameters.get("sharkInitialEnergy"), parameters.get("sharkEnergyGain"));
                    case SAND ->
                      new Sand(config, grid);
                    case LANGTON_LOOP ->
                      new LangtonLoop(config, grid);
                    case SUGAR_SCAPE ->
                      new SugarScape(config, grid);
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid simulation type: " + type));
    }
}
