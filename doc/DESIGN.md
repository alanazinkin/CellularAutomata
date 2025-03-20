# Cell Society Design Final

### 5

### Alana Zinkin, Tatum McKinnis, Angela Predolac

## Team Roles and Responsibilities

* Team Member #1: Alana Zinkin

    * Alana was responsible for the View portion of the simulation, which includes the
      front-end design, CSS styling, and language options.
    * She was also responsible for GUI testing, and she worked on various simulation controller
      methods alongside Angela, such as the starting/stopping/resetting the simulation and allowing
      the user to dynamically adjust the cell shape, grid tiling, edge type, and neighborhood
      arrangement.
    * Alana also contributed various XML files for the simulations and helped integrate the
      front-end and
      back-end for each new simulation.

* Team Member #2: Tatum McKinnis

    * Tatum was responsible for developing the Model portion of the simulation, implementing the
      core functionality of the program. This included designing the Simulation, Cell, and Grid
      classes, as well as the StateInterface interface. She also implemented multiple grid
      topologies, including different edge types (toroidal, mirror, bounded, and infinite) and
      neighborhood structures (Moore, Von Neumann, multiple neighborhoods, and extended Moore).
      Additionally, she developed an infinite grid implementation to support dynamic expansion.
    * Tatum created and implemented a variety of Simulations, including: Darwin Simulation, Conway’s
      Game of Life, Percolation, Spreading Fire, Wa-Tor World, Schelling’s Model of Segregation,
      General Game of Life, Rock-Paper-Scissors, Foraging Ants, Langton’s Loop, Tempesti Loop, and
      SugarScape.
    * In addition to these Simulations, she extended the StateInterface interface for each
      Simulation to define each simulations different states. This included 14 different
      implementations of StateInterface.
    * Beyond developing these simulations, she ensured that the program adhered to design principles
      by structuring the model with well-defined abstractions and creating helper classes to support
      simulation logic and core functionality. This included 43 different classes.
    * Tatum implemented robust exception handling to prevent crashes and ensure the stability of the
      simulation. She accounted for edge cases such as invalid grid states, unexpected cell
      interactions, and edge condition violations in different topologies. By incorporating
      error-checking mechanisms and fail-safe defaults, she improved the program’s reliability,
      ensuring that simulations ran smoothly even in complex scenarios.
    * Tatum also implemented extensive automated testing (including both positive and negative tests
      and utilizing Mock testing) to ensure the correctness of the program. This included 44
      different test classes. Overall, the tests achieved 94% class coverage, 83% method coverage,
      and 81% line coverage.
* Team Member #3: Angela Predolac

## Design goals

Our team's major design goals were to create flexible classes, methods, and easy to read code, such
that future
development would be simple and straight-forward. Our main goal was to ensure that new simulations
could be easily added. We sought to adhere to SOLID design principles, include the Open-Closed
principle,
Single-Responsibility principle, and reducing code redundancies. Our team wanted to make our code as
clear and readable as possible
by using informative names and design patterns to ensure that future developers would be able to
understand our classes and methods.
Another important design goal was to encapsulate our methods appropriately and reduce the number of
public methods. For example, the grid
data structure is encapsulated to ensure that it can be represented any way. We wanted to ensure
that our API made sense and could be used by other teams if necessary.
We made significant refactoring efforts to ensure that our classes and methods were relatively
short. For example,
SimulationController was broken down into over 20 different classes. We also made an effort to add
JavaDoc comments
to all public and protected methods and classes to ensure that other developers would be able to
understand our API.
Finally, we sought to use common design patterns like Strategy and Factory to streamline development
and allow for more flexibility, as
demonstrated by the EdgeStrategy, NeighborhoodStrategy, ShapeStrategy, GridViewFactory, and
CellShapeFactory classes.

#### What Features are Easy to Add

#### Model:

- The model portion was designed to be highly extensible through well-defined abstractions and
  interfaces. It would be easy to add new simulation types because the Simulation class and
  StateInterface were created with extension in mind, requiring only implementation of specific
  state transitions and behaviors for each new simulation. New simulations could be quickly
  implemented by defining their unique states and transition rules without modifying the core
  architecture.

- Adding grid topologies would be straightforward because the edge handling system (toroidal,
  mirror, bounded, infinite) was designed with abstraction through interfaces, allowing
  implementation of new edge behaviors without touching existing code. This makes it easy to
  experiment with different boundary conditions for various simulations.

- Similarly, implementing new neighborhood structures beyond Moore and Von Neumann would be
  uncomplicated due to the modular neighborhood system. New patterns for how cells identify and
  interact with their neighbors could be defined, which would be particularly useful for specialized
  simulations where directional awareness matters.

- The Cell class was designed with extension in mind, making it simple to create new cell types with
  additional properties such as memory of past states, internal variables tracking resources or
  energy, or heterogeneous cells that follow different rule sets within the same simulation. This
  flexibility would allow modeling of more complex systems where cells need to track multiple
  attributes.

- State enhancements would also be straightforward to implement in this design. The existing
  StateInterface implementations could be extended to support continuous values rather than discrete
  states, or to create multi-dimensional state properties to track multiple attributes of a cell
  simultaneously. This would be essential for more complex simulations where cells need to track
  multiple resources and properties at once.

- The robust exception handling and comprehensive testing framework would make these extensions
  reliable, as new features could be thoroughly tested against existing functionality to ensure
  compatibility and prevent regressions. This would allow confident expansion of the simulation
  capabilities without compromising stability.

#### View:

* It is relatively simple to add new cell shapes and grid views since I made abstract classes for
  each and one can simply extend the original class to create a new one.
* It is easy to add front-end components such as the control bars, buttons, and combo-boxes due to
  the user of the UserController class with various methods for making buttons.
* It is easy to add a new simulation to the front-end because the user simply needs to update the
  CSS files and define the CSS tags within the simulation class.
* It was simple to integrate the front-end and the back-end of each new simulation, which means that
  future development should be simple
* It is simply to create a new graph based on cell populations by extending the CellChart class.
* Any new type of display is relatively easy to add by creating a new class and adding the Pane,
  Group, or Canvas to the SimulationView

#### Controller:

## High-level Design

* Our project is broken down into a Model, View, and Controller, and Data folder.
    * The model is responsible for the simulation logic and back-end
    * The view is responsible for the front-end visual display of the simulation and styling
    * The data folder contains XML configuration files for each simulation in a standard format to
      allow
      users to easily create new configurations.
    * The controller is responsible for integrating the model, view, and XML configuration files
      into a fully-functional program.

#### Core Classes

##### Model:

The core of the Model portion consists of several interacting classes that work together to create
and manage cellular automata simulations. The Simulation Class serves as the primary controller for
each specific cellular automaton implementation, managing the simulation's rules, state transitions,
and overall behavior. Each of the 14 different simulations (Conway's Game of Life, Wa-Tor World,
etc.) extends this class to implement its unique logic while maintaining a consistent interface. The
Cell Class represents individual cells within the simulation grid, maintaining their current state
and interacting with neighboring cells according to the rules defined by the specific simulation.
Cells can have various properties depending on the simulation type, such as resource values in
SugarScape. The Grid Class manages the two-dimensional array of cells that forms the simulation
space, handling cell access, neighborhood calculations, and boundary conditions through different
grid topologies (toroidal, mirror, bounded, infinite). The StateInterface defines the possible
states for cells within a specific simulation, with each simulation having its own implementation to
represent its unique set of states. These classes interact in a hierarchical manner where the
Simulation class maintains and updates a Grid object, which contains and organizes multiple Cell
objects, each implementing a specific StateInterface appropriate for its simulation. When a
simulation step occurs, the Simulation class initiates the process, determining the next state for
each Cell in the Grid based on its current state, the states of neighboring Cells, and the
simulation's rules. After all next states are determined, the Cells are updated simultaneously, with
the Grid handling any special conditions related to boundaries or topology.

#### View:

The core of the View portion is the SimulationView class, which is responsible for initializing and
updating each component of the view (ex: Grid, Control Panel, and Charts)

## Assumptions that Affect the Design

#### Model

* The decision to use a standard grid structure with rectangular cells simplified the implementation
  but limited the types of cellular automata that could be easily modeled. While different
  topologies (toroidal, mirror, bounded, infinite) were supported, all relied on a fundamentally
  rectangular grid system rather than supporting arbitrary graph structures or non-uniform cellular
  arrangements, such as cells of different sizes or shapes, cells with varying numbers of neighbors,
  or three-dimensional and higher-dimensional cellular structures.

* The synchronous update mechanism, where all cells are evaluated and then updated simultaneously,
  simplified the programming model but limited the ability to implement asynchronous cellular
  automata rules without significant modifications to the core architecture. This made it easier to
  reason about simulation steps but restricted certain types of emergent behaviors, such as where
  cells update at different times or in different sequences based on internal states or external
  factors.

* The choice to separate state definitions (through the StateInterface) from behavior (in the
  Simulation classes) created a clean separation of concerns but required coordination between these
  components when adding new simulations. This meant that adding a new simulation required
  implementing both a new state interface and corresponding simulation logic, which increased
  development overhead slightly.

* The assumption that cell neighborhoods would be primarily based on standard patterns (Moore, Von
  Neumann) simplified neighborhood calculations but required additional work to support more exotic
  neighborhood configurations. This affected the implementation of simulations with complex
  interaction patterns requiring custom neighborhood structures, such as in the Foraging Ants
  Simulation where directional neighborhoods are used to simulate movement.

* The adoption of a class-based approach for different simulations rather than a more data-driven or
  configuration-based approach meant that adding new simulations required creating new classes
  rather than simply defining rules in a configuration file. This made the codebase more robust but
  less flexible for rapid prototyping of new cellular automata rules

#### View

* The assumption that different GridViews differ based on tiling and shape limits the ability of a
  user to create an entirely different type of Grid where maybe only half the cells are shown to the
  user, or cells are displayed slowly etc. There is a more limited scope of what is considered a
  different GridView.
* There is an assumption that the tiling from the XML file determines what type of GridView should
  be created via reflection. This was really helpful for creating new types of tilings, but changes
  the definition of a GridView. Tiling should have been a separate component of a GridView NOT the
  definition of the GridView.
* There is an assumption that if the tiling is anything other than default in the XML file, the user
  would want to override the cell shape definitions based on state (if present) within the XML file.
  This simplification helps ensure that the tilings appear standard rather than having integrations
  of different shapes within a non-default tiling. However, a developer may be confused as to why a
  certain type of tiling is being implemented if they defined shapes based on states.
* There is an assumption that the user can select the language without the buttons being in their
  prefered language on the initial splash screen. I could have dynamically updated the button text
  when the user selected a language, but since there were only 2 other buttons, this felt
  unnecessary as it would have required a dynamic update.

## Significant differences from Original Plan

#### Model

A few differences emerged between the original design document and the final implementation of the
Model portion of the project. While the original design outlined a State class as a separate enum or
constants class, the final implementation opted for a more flexible StateInterface that was extended
for each simulation to define simulation-specific states. This approach allowed each simulation to
have its own specialized state implementation rather than relying on a generic state class. The Cell
class evolved from simply computing its next state based on simulation rules to tracking both
current and next states simultaneously, which was essential for ensuring synchronous updates across
the grid. The Grid class became much more sophisticated than initially planned, supporting multiple
grid topologies (toroidal, mirror, bounded, infinite) and neighborhood structures (Moore, Von
Neumann, extended Moore) rather than just managing a 2D array of cells. Unlike the original design
where the XML Parser was intended to be part of the Model, in the final implementation, this
functionality was handled by the Controller portion, creating a cleaner separation of concerns. The
original design didn't fully address how to handle the challenge of updating cells based on the
previous generation rather than the current one, but the final implementation resolved this using a
two-state approach where cells maintain both current and next states. Additionally, the Cell class
was enhanced to track previous states, providing a history of cell evolution that wasn't specified
in the original design. The implementation also added robust exception handling and comprehensive
testing that wasn't explicitly detailed in the original design document. These changes collectively
represented a shift toward a more modular and robust architecture than was initially specified.

#### View

* Most of hte original plan remained, but we did not define clear abstractions within our original
  plan, which meant that new abstraction hierarchies were added
    * For example, we originally assumed rectangles were the only cell shape, so when new change
      specifications were created, I made a CellShape class
    * GridView became an abstract class with multiple classes extending it
    * CellChart classes were added with the change specifications
    * A GridSettingsDisplay and HelpView class was added
    * A ShapeStrategy Interface with multiple implementations was created as well to account for the
      different spacings and orientations of shapes within the gridview

## New Features HowTo

##### Model

###### 1. Adding a New Simulation Type

* Create a new class that extends the abstract Simulation class
* Implement the abstract methods, particularly step() which handles the core logic of your
  simulation
* Implement the StateInterface for your new simulation to define all possible states
* Add configuration support for parameters specific to your simulation
* Create comprehensive unit tests for your new simulation

###### 2. Adding a New Grid Topology

* Create a new class that implements the edge strategy interface
* Implement boundary handling methods specific to your new topology
* Register your new edge strategy with the existing edge strategy factory
* Create tests specifically for boundary conditions to verify that cells at the edges behave
  correctly with your new topology

###### 3. Adding a New Neighborhood Structure

* Create a new class that implements the neighborhood strategy interface
* Define how cells identify their neighbors in your new structure
* Register your new neighborhood strategy with the existing neighborhood strategy factory
* Test that cells correctly identify their neighbors with your structure to verify behavior at grid
  boundaries with your new neighborhood pattern

###### 4. Extending Cell Properties

* Create a subclass of Cell that adds new properties and methods
* Add new capabilities like state history, energy levels, or other attributes
* Extend the Grid class to work with your enhanced cells, if necessary
* Extend simulations to utilize the new cell properties
* Test the new cell properties in various scenarios

###### 5. Adding State Enhancements

* Create a new interface that extends StateInterface
* Add methods for new state capabilities
* Implement classes for your enhanced states
* Extend Cell and Simulation classes to work with enhanced states, if necessary

###### 8. Documentation

* Add comprehensive comments to all new classes and methods
* Include examples of how to use new features
* Document any limitations or considerations for the extensions

##### View

###### 1. GridView

1. Within the view/gridview folder:
    1. Create 2 new classes: one called GRID_VIEW_TYPEGridView that extends GridView and
       GRID_VIEW_TYPEGridViewFactory which extends GridViewFactory
    2. Within the GRID_VIEW_TYPEGridView class:
        1. Define 4 private instance variables, which can be found by looking at the HexagonGridView
           class
        2. Define the constructor
        3. Use the Default renderGrid method for default tiling or override if necessary.

###### 2. Shape

1. Within the view/shapefactory folder:
    1. Create 2 new classes: one called SHAPE_TYPECell that implements CellShape and another called
       SHAPE_TYPECellFactory which extends CellShapeFactory
    2. Within The SHAPE_TYPECell class
        1. Create a private instance variable shape object (ex: polygon or shape). This is used for
           the front-end display of the new cell shape.
        2. Override the methods and define the constructor which calls the setDimensions() method
    3. Within the SHAPE_TYPECellFactory class:
        1. Override the createCellShape() class by returning a new SHAPE_TYPECell object

###### 3.Allowing For GridView or Shape Specification in Config Files

1. If user wants to specify the grid view in the config file:
    1. Add tiling tag with exact name of GRID_VIEW_TYPE to specific config file as follows:
       ``` <tiling>GRID_VIEW_TYPE</tiling> ```
2. If a user wants to specify the shape of a specific cell type in the config file:
    1. Add cell_state tag with numeric state and the SHAPE_TYPE as follows:
       ```<cell_state state="NUMERIC_STATE" shape="SHAPE_TYPE"/>```

###### 4. Allowing for Dynamic Update of Cell Tiling

1. Within the view folder:
    1. In the GridSettings Display, update the CELL_TILING_TYPES list to include the name of the new
       GridView type. This should match the tiling options in the config parser class.
2. Within the controller folder:
    1. In the XMLTilingParser, update the list of VALID_TILINGS list to include the name of the new
       GridView type.
       (these two lists should be consolidated in a properties file)

#### Easy to Add Features
**App Preferences:** Although we never implemented app preferences, it is relatively
straight-forward to do:

1. Create an XML file called "AppPreferences.xml" with tags such as <theme>, <language>, etc.
2. Create a Preferences XML parser modeled off of our XML Parser that looks for the theme tag and
   language tag
3. Save it in a preferences map of <key, value> pairs with the keys as the type of preference (
   theme, language, etc.) and the values as the specific preferences
   for a user.
4. Create a front-end component called "Set Preferences" that opens a dialog similar to the Grid
   Settings and allows the user to select their default
   preferences from combo-boxes.
5. When the user presses the "Enter" button, a call should be made to the simulation controller,
   which makes a call to the XML
   Writer class. This class will overwrite the values of the "AppPreferences.xml" file to the new
   preferences that were selected
   in the ComboBoxes (if they were not null). This can be done similar to how a configuration file
   is saved by a user.
6. When the simulation is started, a splash screen appears with language drop down combo boxes and
   theme combo boxes. We can set the default values in
   these combo boxes to be pulled from the app preferences map so that the user does not need to
   update them each time, but has the flexibility
   to change them if desired.

**CELL-50C	Cell Shape: Pentagonal Tiling**:

1. Create a new PentagonGridViewFactory class, PentagonGridView class, PentagonCell, and
   PentagonCellFactory
2. Construct the classes similar to the Hexagon classes. The main difference will be how the
   setDimensions method is overridden.
    1. Pentagons should set 5 points within the set dimensions class.
3. Update the XMLTilingParser class list of valid tilings to include "Pentagon" and update the list
   of valid tilings within the GridSettingsDisplay class (these should be consolidated within a
   properties file)

**CELL-47: Simulation Styles**
1. This feature is nearly updated, but it has not been completed because the parser of the preferences files is incomplete
   2. Debugging the parser would ensure that app preferences can be correctly updated

**CELL-43 Step Simulation**
1. Step Forward is complete, but upon further inspection of the feature requirement, we need to add a stack data structure to each Cell object to maintain its previous history of states
   2. We originally believed - based on the specifications - that step back was only one step back, but we now understand it should be able to progress as many steps back as possible (this can be done with a stack data structure rather than a prev instance variable)
   3. When a user clicks "step back" button, it should pop the previous state value off each Cell's stack. when next state is adopted -> add this value to stack (continue updating as needed) 

**CELL-45A Dynamic Updates: Simulation Parameters**
1. Create a ParametersDisplay abstract class
   1. Using a dialog pane similar to the grid settings class, we can open a dialog displaying various text boxes to update each parameter.
   2. This will be done by writing a method called displayParameterUpdaters, which iterates through the parameters
   map from the simulation configuration and creates a display box for each parameter by calling the method makeParameterUpdater. If the map is empty -> display the text
   "SIMULATION_TYPE has no parameters to update" (which is pulled in from a resource file).
   3. When a user clicks the enter button on the dialog pane -> the backend will check to make sure the value entered is valid (since the map is <String, Double>) and each parameter can take different values, and non-null. If
   the value is valid -> update call the updateParameter(String parameter, Double value) method of the SimulationController class for each parameter,
   which will change the SimulationConfig parameter mapping and the Simulation's instance variables using reflection.
2. Create a ParametersControl concrete class
   1. Create a method called makeParameterUpdater which takes in a string value representing the parameter to be updated
   and outputs a TextBox with the string as the label for the box.


#### Other Features not yet Done
1. CELL-26B: Falling Sand/Water
2. CELL-34	Simulation State
3. CELL-41	Insert Pattern
1. CELL-51D	Dynamic Updates: Undo
2. CELL-46: Darwin: is mostly complete but was never fully implemented due to lack of data files needed for running the sim
3. CELL-49B/C: unclear if neighborhoods are correctly working - need more tests
4. CELL-48B: Mirror edges may require new GridView
