# cell society

## 5

## Alana Zinkin, Tatum McKinnis, Angela Predolac

This project implements a cellular automata simulator.

### Timeline

* Start Date: 1/28/25
* Finish Date: 3/4/25
* Hours Spent: 250 hours

### Attributions

* Resources used for learning (including AI assistance)
* Resources used directly (including AI assistance)

  * ChatGPT used to assist with writing JavaDoc comments
  * ChatGPT used to assist with debugging
  * ChatGPT used to assist with generating JavaFX charts and styling them

### Running the Program

* Main class: Main.java
* Data files needed:

  * User must select a simulation xml file to load a new simulation
  * A language properties file
  * Simulation.properties
  * Version.properties
  * GridSettings.properties
  * InfoDisplay.properties
* Interesting data files:
* Key/Mouse inputs:

  * Zoom in/out: zooms in/out of the grid pane
  * Buttons are selected on click

### Notes/Assumptions

* Assumptions or Simplifications:

  Assumptions:

  1. **Discrete Time Steps**: The simulation progresses in discrete steps rather than continuous time, with all cells updating synchronously.
  2. **Grid-Based World**: All simulations operate on a rectangular grid of cells, with no support for irregular grids or continuous spaces.
  3. **State-Based Behavior**: Cell behavior is entirely determined by its current state and the states of its neighbors, with no other external influences.
  4. **Complete Information**: Each cell has access to all relevant neighborhood information needed to determine its next state.

Simplifications:

1. **Simple State Representation**: States are represented as enumerated values rather than complex objects with internal properties.
2. **Fixed Grid Dimensions**: Grid size is established at initialization and remains constant throughout the simulation (except in specialized grid implementations).
3. **Limited History**: The model tracks only one previous state for step-back functionality, rather than maintaining a complete simulation history.
4. **Integer-Based State Values**: States are mapped to integers for serialization and configuration, which simplifies storage but limits state complexity.

* Known Bugs:
* Features implemented:

  * CELL-01 through CELL-16
  * CELL-26A, CELL-26C, CELL-26D, CELL-26E, CELL-26F
  * CELL-27, CELL-28, CELL-29, CELL-30, CELL-31
  * CELL-35, CELL-36, CELL-37, CELL-38, CELL-40, CELL-42, CELL-43, CELL-45C, CELL45D
  * CELL-46
  * CELL-48A, CELL-48B, CELL-48C, CELL-48D
  * CELL-49A, CELL-49B, CELL-49C, CELL-49D
  * CELL-50A, CELL-50B, CELL-50X
  * CELL-52, CELL-53A, CELL-53B, CELL-53C, CELL-53E
* Features unimplemented:

  * CELL-50C, CELL-51D, CELL-51X, CELL-53X
* Noteworthy Features:

  * Users can dynamically update the grid tiling structure and change the theme of the entire view
  * Users can also see different graphs for each simulation to see total cell populations over time
  * Users can run multiple simulations simultaneously
* Running multiple simulations simultaneously:

  * All simulations run independently and are created by
    initializing a new SimulationMaker object and calling makeSimulation()
  * Each simulation runs in an entirely new window, which is not organized in any particular arrangement

### Assignment Impressions

* This assignment was an intellectually stimulating challenge that forced our team to
  learn SOLID design principles, new ways of instantiating objects, such as reflection, how to test both the back-end and
  the front-end using J-unit and TestFX, and new design patterns that we can incorporate into our projects for a stramlined design.
* This project appeared very difficult upon first reading the feature requirements, but our team was able to work really well together
  to integrate the front-end and back-end components to create a fully-functional project.
* The project taught us that well-designed code makes future development significantly easier. For example, we were ale to create over 10 different
  simulations with different rules, yet use the same front-end code for all of them.
