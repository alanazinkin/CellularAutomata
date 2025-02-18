package cellsociety.model.simulations;

import cellsociety.model.Cell;
import cellsociety.model.Grid;
import cellsociety.model.state.SandState;
import cellsociety.controller.SimulationConfig;

public class Sand {
  private static final int SAND = 1;
  private static final int WALL = 2;
  private static final int WATER = 3;

  private final Grid grid;
  private final int rows;
  private final int cols;

  public Sand(SimulationConfig config, Grid grid) {
    this.grid = grid;
    this.rows = grid.getRows();
    this.cols = grid.getCols();
  }

  public void step() {
    Cell[][] nextStates = calculateNextStates();
    applyNextStates(nextStates);
  }

  private Cell[][] calculateNextStates() {
    Cell[][] nextStates = createEmptyStateGrid();
    copyWalls(nextStates);
    processSand(nextStates);
    processWater(nextStates);
    return nextStates;
  }

  private Cell[][] createEmptyStateGrid() {
    Cell[][] newGrid = new Cell[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        newGrid[r][c] = new Cell(SandState.EMPTY);
      }
    }
    return newGrid;
  }

  private void copyWalls(Cell[][] nextStates) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (isWall(r, c)) {
          nextStates[r][c].setCurrentState(SandState.WALL);
        }
      }
    }
  }

  private void processSand(Cell[][] nextStates) {
    for (int r = rows - 1; r >= 0; r--) {
      for (int c = 0; c < cols; c++) {
        if (isSand(r, c)) {
          moveSand(r, c, nextStates);
        }
      }
    }
  }

  private void moveSand(int r, int c, Cell[][] nextStates) {
    boolean moved = tryMove(r, c, r + 1, c, nextStates); // Only move down

    if (!moved && nextStates[r][c].getCurrentState() == SandState.EMPTY) {
      nextStates[r][c].setCurrentState(SandState.SAND);
    }
  }

  private void processWater(Cell[][] nextStates) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (isWater(r, c)) {
          moveWater(r, c, nextStates);
        }
      }
    }
  }

  private void moveWater(int r, int c, Cell[][] nextStates) {
    boolean moved = tryMove(r, c, r + 1, c, nextStates); // Down

    if (!moved) {
      boolean goLeftFirst = Math.random() < 0.5;
      if (goLeftFirst) {
        moved = tryMove(r, c, r, c - 1, nextStates);
        if (!moved) moved = tryMove(r, c, r, c + 1, nextStates);
      } else {
        moved = tryMove(r, c, r, c + 1, nextStates);
        if (!moved) moved = tryMove(r, c, r, c - 1, nextStates);
      }
    }

    if (!moved && nextStates[r][c].getCurrentState() == SandState.EMPTY) {
      nextStates[r][c].setCurrentState(SandState.WATER);
    }
  }

  private boolean tryMove(int currR, int currC, int targetR, int targetC, Cell[][] nextStates) {
    if (!isValid(targetR, targetC)) return false;
    if (grid.getCell(targetR, targetC).getCurrentState() == SandState.WALL) return false;
    if (nextStates[targetR][targetC].getCurrentState() != SandState.EMPTY) return false;

    nextStates[targetR][targetC].setCurrentState(grid.getCell(currR, currC).getCurrentState());
    return true;
  }

  private void applyNextStates(Cell[][] nextStates) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell original = grid.getCell(r, c);
        original.setCurrentState(nextStates[r][c].getCurrentState());
      }
    }
  }

  private boolean isValid(int r, int c) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
  }

  private boolean isSand(int r, int c) {
    return grid.getCell(r, c).getCurrentState() == SandState.SAND;
  }

  private boolean isWater(int r, int c) {
    return grid.getCell(r, c).getCurrentState() == SandState.WATER;
  }

  private boolean isWall(int r, int c) {
    return grid.getCell(r, c).getCurrentState() == SandState.WALL;
  }

  public void setCellState(int row, int col, int state) {
    Cell cell = grid.getCell(row, col);
    switch (state) {
      case SAND -> cell.setCurrentState(SandState.SAND);
      case WALL -> cell.setCurrentState(SandState.WALL);
      case WATER -> cell.setCurrentState(SandState.WATER);
    }
  }
}