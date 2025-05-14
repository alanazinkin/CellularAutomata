# Cell Society
## Team 5
## Alana Zinkin, Tatum McKinnis, Angela Predolac


This project implements a cellular automata simulator.

### Timeline

 * Start Date: 1/28/25

 * Finish Date: 3/4/25

 * Hours Spent: 250 hours


### Attributions

 * Resources used for learning (including AI assistance)
   * [Medium Article](https://adam-carroll.medium.com/user-interface-testing-with-testfx-5747ba02b0ec) used
   to assist with TestFX
   * [Refactoring Guru](https://refactoring.guru/design-patterns/factory-method) used to learn about design patterns
   * [Assignment Guideline](https://courses.cs.duke.edu/compsci308/spring25/assign/02_simulation/nifty/scott-wator-world/WatorWorld.htm#Histogram) for graph to assist with JavaFX chart display
   * [SugarScape Discussion](https://greenteapress.com/complexity/html/thinkcomplexity012.html#fig.notax) used to help with chart display
   * [Hexagonal Tiling Site](https://mathworld.wolfram.com/HexagonalGrid.html)
 
 * Resources used directly (including AI assistance)
   * ChatGPT used to assist with writing JavaDoc comments
   * ChatGPT used to assist with debugging 
   * ChatGPT used to assist with generating JavaFX charts and styling them

### Running the Program

 * Main class: Main.java

 * Data files needed: 
   * User must select a simulation xml file to load a new simulation
   * help.html
   * A language properties file
   * Simulation.properties
   * Version.properties
   * GridSettings.properties
   * InfoDisplay.properties

 * Key/Mouse inputs:
   * Zoom in/out: zooms in/out of the grid pane
   * Buttons are selected on click


### Notes/Assumptions

 * Assumptions or Simplifications:
   * I assumed that a user is running this simulation on a computer screen wide enough to support all
   the elements of the simulation. If the user is running this on a small computer, they might not be able to view 
   all the buttons or components

 * Known Bugs:
   * Ant Simulation displays states as memory values, rather than their string value

 * Features implemented:
   * CELL-01 through CELL-16
   * CELL-26A, CELL-26C, CELL-26D, CELL-26E, CELL-26F
   * CELL-27, CELL-28, CELL-29, CELL-30, CELL-31
   * CELL-35, CELL-36, CELL-37, CELL-38, CELL-40, CELL-42, CELL-43, CELL-45C, CELL45D
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


