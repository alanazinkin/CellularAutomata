package cellsociety.Model.Simulations;
import cellsociety.Model.Cell;
import cellsociety.Model.Grid;
import cellsociety.Model.Simulation;
import cellsociety.Model.State.PercolationState;

import cellsociety.Model.StateInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * A simulation of percolation that models fluid flow through a porous medium.
 * <p>
 * This cellular automaton simulation uses a grid of cells where each cell has one of three states:
 * <ul>
 *   <li>{@link PercolationState#BLOCKED} – the cell is blocked and cannot be percolated.</li>
 *   <li>{@link PercolationState#OPEN} – the cell is open, but not yet percolated.</li>
 *   <li>{@link PercolationState#PERCOLATED} – the cell is open and has been percolated.</li>
 * </ul>
 * <p>
 * The simulation applies the following rules at each time step:
 * <ol>
 *   <li>If a cell is {@code BLOCKED}, it remains {@code BLOCKED}.</li>
 *   <li>If a cell is already {@code PERCOLATED}, it remains {@code PERCOLATED}.</li>
 *   <li>If a cell is {@code OPEN} and at least one cell in its eight-cell (Moore) neighborhood
 *       is {@code PERCOLATED}, then it becomes {@code PERCOLATED} in the next state.</li>
 *   <li>Otherwise, an {@code OPEN} cell remains {@code OPEN}.</li>
 * </ol>
 * <p>
 * Note: This implementation is deterministic; however, the original specification mentions a stochastic
 * element with probabilities. If stochastic behavior is required, additional logic (e.g., random number generation)
 * can be integrated into the transition function.
 */
public class Percolation extends Simulation {

  /**
   * Constructs a new {@code PercolationSimulation} instance that operates on the specified grid.
   *
   * @param grid the {@code Grid} on which the percolation simulation is performed
   */
  public Percolation(Grid grid) {
    super(grid);
  }

  @Override
  public Map<StateInterface, Color> initializeStateMap() {
    stateMap = new HashMap<>();
    stateMap.put(PercolationState.OPEN, Color.WHITE);
    stateMap.put(PercolationState.PERCOLATED, Color.LIGHTBLUE);
    stateMap.put(PercolationState.BLOCKED, Color.BLACK);
    return stateMap;
  }

  /**
   * Applies the percolation rules to all cells in the grid.
   * <p>
   * The rules are applied as follows:
   * <ul>
   *   <li>If the current state of a cell is {@link PercolationState#BLOCKED}, the cell remains unchanged.</li>
   *   <li>If the current state is {@link PercolationState#PERCOLATED}, the cell remains unchanged.</li>
   *   <li>If the current state is {@link PercolationState#OPEN} and at least one cell in its eight-cell
   *       Moore neighborhood is {@code PERCOLATED}, then the cell's next state is set to {@code PERCOLATED}.</li>
   *   <li>Otherwise, an {@code OPEN} cell remains {@code OPEN}.</li>
   * </ul>
   *
   * <p>
   * After this method is called, the grid's {@link Grid#applyNextStates()} method should be invoked to update
   * all cells to their next state.
   */
  @Override
  public void applyRules() {
    int rows = grid.getRows();
    int cols = grid.getCols();

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell cell = grid.getCell(i, j);
        PercolationState currentState = (PercolationState) cell.getState();

        // BLOCKED and PERCOLATED cells remain unchanged.
        if (currentState == PercolationState.BLOCKED ||
            currentState == PercolationState.PERCOLATED) {
          cell.setNextState(currentState);
          continue;
        }

        // For OPEN cells, check if any neighbor is PERCOLATED.
        List<Cell> neighbors = grid.getNeighbors(i, j);
        boolean willPercolate = false;
        for (Cell neighbor : neighbors) {
          if (neighbor.getState() == PercolationState.PERCOLATED) {
            willPercolate = true;
            break;
          }
        }
        if (willPercolate) {
          cell.setNextState(PercolationState.PERCOLATED);
        } else {
          cell.setNextState(PercolationState.OPEN);
        }
      }
    }
  }
}

