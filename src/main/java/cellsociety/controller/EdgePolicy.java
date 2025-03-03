package cellsociety.controller;

/**
 * Possible edge policies for the grid.
 * @author angelapredolac 
 */
public enum EdgePolicy {
    FINITE,     // Cells at the edge have fewer neighbors
    TOROIDAL,   // Grid wraps around (top connects to bottom, left to right)
    INFINITE    // Grid extends infinitely with a default state
}
