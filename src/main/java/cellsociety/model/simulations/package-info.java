package cellsociety.model.simulations;
/**
 * Provides implementations of various cellular automata simulations.
 *
 * <p>This package contains concrete implementations for different cellular automata
 * models including Game of Life, Ant Simulation, Fire propagation, and others. Each simulation
 * follows a common interface while implementing specific rules and behaviors.
 *
 * <h2>Design Goals</h2>
 * <p>The simulation implementations aim to:
 * <ul>
 *   <li>Present a unified interface for different simulation types</li>
 *   <li>Support simulation control (step, start, pause, reset)</li>
 *   <li>Enable efficient computation for large simulation spaces</li>
 *   <li>Maintain separation between simulation logic and visualization</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <p>Implementations in this package adhere to these contracts:
 * <ul>
 *   <li>State changes occur atomically during step operations</li>
 *   <li>Simulations respect their defined boundary conditions</li>
 * </ul>
 *
 * @since 1.0
 */