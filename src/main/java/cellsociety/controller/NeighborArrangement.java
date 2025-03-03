package cellsociety.controller;

/**
 * Possible neighbor arrangements for the grid.
 * @author angelapredolac
 */
public enum NeighborArrangement {
    MOORE,          // All 8 surrounding cells for square grid
    VON_NEUMANN,    // Only orthogonal neighbors (N, E, S, W)
    CUSTOM          // Custom neighbor definition
}
