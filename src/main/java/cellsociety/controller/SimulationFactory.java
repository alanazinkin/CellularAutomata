package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.simulations.Fire;
import cellsociety.model.simulations.GameOfLife;
import cellsociety.model.simulations.Percolation;
import cellsociety.model.simulations.Schelling;

public class SimulationFactory {
    public static Simulation createSimulation(String type, SimulationConfig config,
                                              Grid grid, SimulationParameters params) {
        return SimulationController.SimulationType.fromString(type)
                .map(simType -> switch (simType) {
                    case GAME_OF_LIFE -> new GameOfLife(config, grid);
                    case SPREADING_FIRE ->
                            new Fire(config, grid, params.fireProb(), params.treeProb());
                    case PERCOLATION ->
                            new Percolation(config, grid, params.percolationProb());
                    case SCHELLING ->
                            new Schelling(config, grid, params.satisfaction());
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid simulation type: " + type));
    }
}
