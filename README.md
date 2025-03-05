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

 * Interesting data files: bacteria_default.xml, game_of_life_default.xml (implementing the new style changes)

 * Key/Mouse inputs:
   * Zoom in/out: zooms in/out of the grid pane
   * Buttons are selected on click


### Notes/Assumptions

 * Assumptions or Simplifications:

 * Known Bugs:
   * Langton Loop Reset does not reset to original state, but rather a different original state
   * Ant Simulation is not working properly

 * Features implemented:
   * CELL-01 through CELL-16
   * CELL-26A, CELL-26C, CELL-26D, CELL-26E, CELL-26F
   * CELL-27, CELL-28, CELL-29, CELL-30, CELL-31
   * CELL-35, CELL-36, CELL-37, CELL-38, CELL-40, CELL-42, CELL-43, CELL-45C, CELL45D
   * CELL47
   * CELL-50A, CELL-50B, CELL-50X
   * CELL-52, CELL-53A, CELL-53B, CELL-53C, CELL-53E

 * Features unimplemented:
   * CELL-50C, CELL-51D, CELL-51X, CELL-53X 

 * Noteworthy Features:
   * Users can dynamically update the grid tiling structure and change the theme of the entire view
   * Users can also see different graphs for each simulation to see total cell populations over time
   * Users can run multiple simulations simultaneously
   * XMLWriter and XMLParser were abstracted to support JSON files as well (although not tested on that yet)

 * Running multiple simulations simultaneously:
   * All simulations run independently and are created by 
   initializing a new SimulationMaker object and calling makeSimulation()
   * Each simulation runs in an entirely new window, which is not organized in any particular arrangement


### Assignment Impressions
* This assignment was an intellectually stimulating challenge that forced our team to
learn SOLID design principles, new ways of instantiating objects, such as reflection, how to test both the back-end and
the front-end using J-unit and TestFX, and new design patterns that we can incorporate into our projects for a streamlined design.
* This project appeared very difficult upon first reading the feature requirements, but our team was able to work really well together
to integrate the front-end and back-end components to create a fully-functional project.
* The project taught us that well-designed code makes future development significantly easier. For example, we were ale to create over 10 different
simulations with different rules, yet use the same front-end code for all of them.


