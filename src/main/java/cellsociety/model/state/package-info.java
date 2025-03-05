package cellsociety.model.state;
/**
 * Provides state implementations for different cellular automata simulations.
 *
 * <p>This package contains various state enumerations that implement the
 * {@link cellsociety.model.StateInterface},
 * including {@link cellsociety.model.state.GameOfLifeState},
 * {@link cellsociety.model.state.FireState}, {@link cellsociety.model.state.PercolationState},
 * {@link cellsociety.model.state.SchellingState}, and
 * {@link cellsociety.model.state.WaTorWorldState}. These state implementations define the possible
 * cell conditions within their respective simulations.
 *
 * <h2>Design Goals</h2>
 * <p>The state implementations aim to:
 * <ul>
 *   <li>Encapsulate state-specific properties within enumerated values</li>
 *   <li>Provide consistent access to state information across different simulations</li>
 *   <li>Enable polymorphic handling of different state types through a common interface</li>
 *   <li>Support both textual and numeric representation of states</li>
 *   <li>Facilitate state mapping for configuration and visualization</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <p>State implementations in this package adhere to these contracts:
 * <ul>
 *   <li>All state enums must implement the StateInterface</li>
 *   <li>Each state must have a unique numeric value within its simulation context</li>
 *   <li>State values must be consistent throughout the simulation lifecycle</li>
 *   <li>State implementations should be immutable</li>
 *   <li>Methods must not throw exceptions during normal operation</li>
 * </ul>
 *
 * @since 1.0
 */