## DESIGN OVERVIEW
Our team's major design goals were to create flexible classes, methods, and easy to read code, such that future
development would be simple and straight-forward. Our main goal was to ensure that new simulations
could be easily added. We sought to adhere to SOLID design principles, include the Open-Closed principle,
Single-Responsibility principle, and reducing code redundancies. Our team wanted to make our code as clear and readable as possible
by using informative names and design patterns to ensure that future developers would be able to understand our classes and methods.
Finally, we sought to use common design patterns like Strategy and Factory to streamline development and allow for more flexibility.

#### DESIGN 1 (CHANGED) : GridView creation

**Project Start**

* Abstract GridView class with a grid for each type of simulation
* No class for each cell shape, assume cells can only be rectangles (BAD ASSUMPTION!)

**Mid-Project Change**

* Keep abstract grid view class and create a concrete class for each tiling pattern
* Create factory class to initialize a GridView based on "tiling" configuration pattern
* Create CellShape interface and implement it for each type of cell shape
* Create a CellShape Factory for each shape to initialize them

**Feature Helped**

* BENEFITS:
  * Developers can easily add new GridViews for each shape
  * Users can create a default grid view, which integrates various kinds of shapes
  * Users can select different cell states to be represented by different shapes
  * Users can override specific cell state setting by selecting a different tiling patter

**Feature Challenged**

* TRADE-OFFS:
  * Four classes must be created for each type of tiling pattern because of the use of reflection
    to
    create a new GridView and CellShape (lots of overhead)
  * Some classes, like hexagon and parallelogram had VERY similar structures and some duplicated
    code

#### DESIGN 2 (STABLE) : SimulationController

**Feature Helped**

* BENEFITS:
    * 

## TESTING OVERVIEW

#### TEST 1:

applyRules\_OpenCellWithPercolatedNeighbor\_BecomesPercolated() in PercolationTest

1. Verifies that when a percolation probability is set to 1 (100%), all OPEN cells adjacent to a PERCOLATED cell will become PERCOLATED after one simulation step.
2. Chose a percolation probability of 1 (100%) because this guarantees that percolation will occur, making it easier to verify the specific behavior where open cells next to percolated cells become percolated themselves

   ```
     /**
      * applyRules: Open cells adjacent to a percolated cell become percolated with probability 1.
      * Input: Cell (0,0) is set to PERCOLATED and probability is set to 1.0.
      */
     @Test
     void applyRules_OpenCellWithPercolatedNeighbor_BecomesPercolated() {
       Grid grid = createTestGrid();
       grid.getCell(0, 0).setCurrentState(PercolationState.PERCOLATED);
       SimulationConfig simConfig = createSimulationConfigForPercolation();
       Percolation simulation = new Percolation(simConfig, grid, PROBABILITY_ONE);

       simulation.applyRules();
       grid.applyNextStates();

       // All adjacent cells should become percolated due to propagation.
       assertEquals(PercolationState.PERCOLATED, grid.getCell(0, 1).getCurrentState(),
           "Cell (0,1) should become percolated.");
       assertEquals(PercolationState.PERCOLATED, grid.getCell(1, 0).getCurrentState(),
           "Cell (1,0) should become percolated.");
       assertEquals(PercolationState.PERCOLATED, grid.getCell(1, 1).getCurrentState(),
           "Cell (1,1) should become percolated.");
     }
   ```

#### TEST 2:

setCurrentState\_NullValue\_ThrowsIllegalArgumentException() in CellTest

1. Specifically checks that the setCurrentState() method properly validates its input by attempting to set the cell’s current state to null and verifies that this attempt throws an IllegalArgumentException
2. Chose to test with null because the class manages multiple states (current, next, previous) and these states must be maintained throughout state transitions to protect the cell’s transition logic. Null values would compromise this system, especially during operations like applyNextState() that rely on valid states.

```
 /**
   * Tests setting the current state to null.
   * <p>
   * Verifies that setting the state to null throws an {@link IllegalArgumentException}.
   * </p>
   */
  @Test
  void setCurrentState_NullValue_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> cell.setCurrentState(null),
        "Setting state to null should throw IllegalArgumentException");
  }
```

#### TEST 3:

applyRules\_LiveCellWithTwoLiveNeighbors\_Survives() in GameOfLifeTest

1. Verifies one of the core rules of Conway’s Game of Life: a live cell with exactly 2 live neighbors should survive to the next generation
2. Chose to test with exactly two neighbors because this is a key survival rule. It is one of the specific conditions where a live cell should continue living, making it a critical functionality to verify.

```
  /**
   * applyRules: A live cell with exactly 2 live neighbors survives. Input: Center cell is ALIVE
   * with exactly 2 live neighbors.
   */
  @Test
  void applyRules_LiveCellWithTwoLiveNeighbors_Survives() {
    Cell center = grid.getCell(1, 1);
    center.setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(0, 0).setCurrentState(GameOfLifeState.ALIVE);
    grid.getCell(0, 1).setCurrentState(GameOfLifeState.ALIVE);

    simulation.applyRules();

    assertEquals(GameOfLifeState.ALIVE, center.getNextState(),
        "A live cell with 2 live neighbors should survive.");
  }
```

#### TEST 4: testInvalidRuleString in RuleStringParserTest
1. Verifies that the RuleStringParser throws an IllegalArgumentException when the rule string is invalid (missing a separator, containing a digit beyond 0-8, or just contains invalid characters)
2. Chose to test with invalid rule strings that could potentially “break” the simulation and make sure the appropriate exception would be thrown instead of setting up the simulation incorrectly or causing bugs in the program.
```
@Test
void testInvalidRuleString() {
assertThrows(IllegalArgumentException.class, () -> new RuleStringParser("B39/S23")); // 9 is invalid
assertThrows(IllegalArgumentException.class, () -> new RuleStringParser("B3S23")); // Missing separator
assertThrows(IllegalArgumentException.class, () -> new RuleStringParser("Hello")); // Invalid format
}
```

#### TEST 5: testParseXMLFile_MissingRequiredFields in XMLParserTest
1. Verifies that a ConfigurationException is thrown when an XML file with missing required fields is passed into the parser.
2. Chose to test with missing required fields to make sure that the parser recognizes that a required field is missing to correctly set up the simulation; this also depends on which fields are required for each simulation type.
```
@Test
void testParseXMLFile_MissingRequiredFields(@TempDir Path tempDir) throws IOException, ConfigurationException {
File incompleteXmlFile = createIncompleteXmlFile(tempDir);

        doNothing().when(mockFileValidator).validateFile(incompleteXmlFile.getAbsolutePath());

        assertThrows(ConfigurationException.class, () -> {
            xmlParser.parseXMLFile(incompleteXmlFile.getAbsolutePath());
        });
    }
```
#### TEST 6: testValidateStyleXMLStructure_valid in XMLStyleParserTest
1. Verifies that an exception is not thrown when a valid XML style file is being parsed.
2. Chose to test to make sure that valid files correctly pass the validation stage before being parsed.
```
@Test
void testValidateStyleXMLStructure_valid() throws Exception {
Document document = loadDocument(validXMLFile);
assertDoesNotThrow(() -> parser.validateStyleXMLStructure(document));
}
```
#### TESTFX TEST 1:

```
@Test
public void makeGridLinesToggleButton_clickButtonOnce_GridLinesRemoved() {
SimulationConfig simulationConfig = new SimulationConfig("Game of Life", "title", "Alana Zinkin", "Description",
5, 5, myInitialStates, myParameters,"Default");
myController = new SimulationController();
GridView gridView = new DefaultGridView(myController, simulationConfig, myGrid);
Button gridLinesToggleButton = myUserController.makeGridLinesToggleButton("Gridlines");
myUserController.setGridLinesButtonAction(gridView, gridLinesToggleButton);
interact(() -> {
myRoot.getChildren().add(gridLinesToggleButton);
try {
gridView.createGridDisplay(myRoot, mySimulation.getColorMap(), simulationConfig);
} catch (ClassNotFoundException e) {
throw new RuntimeException(e);
clickOn(gridLinesToggleButton);
});
for (Shape cell: gridView.getImmutableCellsList()) {
assertEquals(0, cell.getStrokeWidth());
}
}
```

#### TESTFX TEST 2:

```
  @Test
public void createGridView_HexagonViewSelected_GridNotNull()
throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

int[] initialStates = new int[]{2, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1};
Map<String, Double> parameter = new HashMap<>();
parameter.put("percolationProb", 0.7);
mySimulationConfig = new SimulationConfig("Percolation", "Basic Percolation", "Alana", "Description",
6, 6, initialStates, parameter, "Hexagon");
myGrid = new Grid(6, 6, PercolationState.PERCOLATED);
int numCols = myGrid.getCols();
int numRows = myGrid.getRows();
double expectedCellWidth = (0.7 * 800 / numCols);
double expectedCellHeight = (0.7 * 600 / numRows);
runAsJFXAction(() -> {
GridView gridView = new HexagonGridView(myController, mySimulationConfig, myGrid);
try {
gridView.createGridDisplay(myRoot, mySimulation.getColorMap(), mySimulationConfig);
assertEquals(expectedCellWidth, gridView.getCellWidth());
assertEquals(expectedCellHeight, gridView.getCellHeight());
List<Shape> cells =  gridView.getImmutableCellsList();
assertEquals(36, cells.size());
Pane pane = gridView.getGridPane();
for (Shape shape : cells) {
assert(pane.getChildren().contains(shape));
}
    } catch (ClassNotFoundException e) {
throw new RuntimeException(e);
});
}
```

#### TESTFX TEST 3:

```
  @Test
void setupControlBar_ControlBarExists_ControlsAreShown() {
ControlPanel testControlPanel = new ControlPanel(myStage, myScene, myController, mySimView, myResources, myGridView);
runAsJFXAction(() -> {
testControlPanel.setupControlBar(mySimView.getRoot());
HBox myControlBar = lookup("#myRoot #controlBar").query();

Button startButton = lookup("#startButton").query();
Button pauseButton = lookup("#pauseButton").query();
Button stepForwardButton = lookup("#stepForwardButton").query();
Button stepBackwardButton = lookup("#stepBackButton").query();
Button resetButton = lookup("#resetButton").query();
Button saveButton = lookup("#saveButton").query();
Button addSimButton = lookup("#addSimButton").query();
ComboBox
<String> simTypeComboBox = lookup("#simulationTypesComboBox").query();
  ComboBox
  <String> configFileComboBox = lookup("#configFileComboBox").query();
    List
    <Button> buttons = List.of(startButton, pauseButton, stepForwardButton, stepBackwardButton,
      resetButton, saveButton, addSimButton);
      assertTrue(myControlBar.getChildren().containsAll(buttons), "Not all buttons are in the
      control bar.");
      assertTrue(myControlBar.getChildren().contains(simTypeComboBox), "Doesnt contain sim type
      combo box.");
      assertTrue(myControlBar.getChildren().contains(configFileComboBox), "Doesnt contain config
      file combo box.");
      });
      }
```

## API OVERVIEW

### API 1:

Key Abstractions and Public Methods:

1. Simulation: Abstract base class that manages cellular automata simulations by controlling state transitions, applying rules, and tracking statistics across a grid of cells.

   1. step(): Executes one simulation iteration
   2. stepBackOnce(): Reverts to previous state (if available)
   3. applyRules(): Implement simulation-specific logic
2. Grid: 2D container of cells that handles neighbor relationships, edge behaviors, and state updates using configurable strategies for boundaries and neighborhood patterns.

   1. getCell(int row, int col): Access cells with edge strategy applies
   2. getNeighbors(int row, int col): Get neighbors based on neighborhood strategy
   3. applyNextStates() / applyPreviousStates(): Update all cells to their next states / Revert cells to previous states if available
   4. setEdgeStrategy(EdgeStrategy) / setNeighborhoodStrategy(NeighborhoodStrategy): Change edge handling behavior / neighborhood pattern
3. Cell: Fundamental unit that maintains its current, next, and previous states while providing methods to transition between them during simulation steps.

   1. getCurrentState() / setCurrentState(StateInterface), getNextState() / setNextState(StateInterface), getPrevState() : State accessors and mutators
   2. applyNextState() / applyPrevState(): Transition to next state / Revert to previous state
4. StateInterface: An interface that defines the common behavior for all possible cell states, requiring implementations to provide string and numeric representations.

   1. getStateValue(): String representation of state
   2. getNumericValue(): Integer representation of state
5. Edge Strategy: An interface that defines different ways to handle grid boundaries by determining valid positions and providing cell access methods with appropriate edge behavior.

   1. isValidPosition(Grid, int, int): Checks if the given coordinates are valid according to this edge strategy’s rules
   2. getCell(Grid, int, int): Retrieves the cell at specified coordinates, applying appropriate boundary handling
   3. getType(): Returns a unique string identifier for the edge strategy
6. Neighborhood Strategy: An interface that defines which cells are considered neighbors by providing methods to determine the coordinates of neighboring cells for a given position.

   1. getNeighborCoordinates(int, int): Returns a list of coordinate pairs representing all neighbors of a cell
   2. getType(): Returns a unique string identifier for the neighborhood strategy

Open for Extension:

1. Template Method Pattern:

   1. Simulation provides a framework where subclasses need only implement specific abstract methods
   2. New simulations can be added without modifying core functionality
2. Strategy Pattern:

   1. Edge handling is encapsulated in EdgeStrategy implementations. Neighborhood patterns are defined by NeighborhoodStrategy implementations.
   2. New strategies can be created by implementing these interfaces.
      1. For example, grid.getNeighbors() uses the current neighborhood strategy to determine which cells are neighbors rather than having hardcoded behavior.
3. State Pattern:

   1. Different state implementations share a common interface
   2. New state types can be added without changing core cell behavior
4. Composition

   1. Examples:
      1. Grid is composed of Cells which means new Grid types can be created without affecting cell behavior, different cell implementations can be used without changing grid, etc.
      2. Cells are composed of a State which means Cells can change their state dynamically, states can be shared across multiple cells, new states can be introduced without modifying the Cell class, etc.
      3. Grid is composed of Strategies which means you can combine any edge strategy with any neighborhood strategy, you can add new strategies, etc.

Support for readable, well-designed code for users/teammates:

1. Strong encapsulation

   1. Private variables with public accessors
   2. Centralized validation methods
      1. Example: Cell class, validateState centralizes the null-checking logic for state validation and is reused throughout the Cell class (constructor, setCurrentState, setNextState, resetState)
2. Error Prevention

   1. Comprehensive input validation
      1. Example: Grid constructor validation (checks for valid dimensions), Simulation validateInititalStates (checks for correct size)
   2. Clear specific error messages:
      1. Example: Constants for error messages in Simulation
   3. Null Checks with descriptive error messages:
      1. Example: Grid constructor checks for non-null state, edgeStrategy, and neighborhood strategy with context-specific error messages
3. Consistent naming conventions

   1. Methods follow clear verb-noun structure
      1. Example: Cell class methods (setCurrentState, applyNextState, etc.)
   2. Consistent Naming Patterns across related classes
      1. Example: In Cell - applyNextState() and applyPrevState(). In Grid - applyNextStates() and applyPreviousStates()
   3. Abstract methods clearly indicate their purpose
      1. Example: In Simulation, applyRules().
4. Separation of concerns

   1. Grid manages spatial relationships
      1. Maintain 2D structure of Cells and their relationships, handles neighbor relationships, applies edge strategies
   2. Cell manages state transitions
      1. Maintain current, next, and previous states, validates state assignments
   3. Simulation manages rules and execution
      1. Controls simulation lifestyle through step(), manages state mappings
   4. States represent only the state values
      1. Define the possible states a Cell can be in, provide numeric and string representations of those states
5. Javadoc Documentation

   1. Thorough class and method documentation with examples of valid input, preconditions, and exceptions

Key Implementation details it hides:

1. Cell State transition mechanism - internal state management of Cells is hidden from external code

   1. Private state variables
   2. Hidden history mechanism with clean public interface
      1. When stepping back, the code uses a special marker to indicate that a previous state has already been applied.
      2. API exposes a simple boolean method to indicate success/failure of stepping back (stepBackOnce())
2. Grid internals - internal structure of the Grid is protected from external manipulations

   1. Protected cell array (private and never directly exposed)
   2. Hidden initialization - Cell creation is completely encapsulated in a private method (initializeCells(StateInteface state))
3. Validation logic - validation mechanics are hidden from public view

   1. Validation logic encapsulated in private methods and error messages are defined as private constants
4. Simulation iteration management - Details of how the simulation tracks progress are hidden

   1. Iteration counter is private, reset functionality (resetIterationCount()) is private
5. State Management - How states are mapped and represented is internal

   1. State and color maps are private

Use Case to show its effectiveness:

Percolation

1. Percolation Class and PercolationState Enum each have a single focused responsibility

   1. Percolation Class: implements only simulation specific rules
   2. State pattern implementation - PercolatonState Enum: Defines only state values, not behavior
2. Extensibility Without Modification

   1. Example: Strategy pattern utilization. The same Percolation Simulation works with different edge strategies and neighborhood strategies (using getNeighbors(), for example).
3. Template Method pattern in action

   1. Implements abstract methods defined in Simulation base class. Shows how the template method pattern enables customization while maintaining structure.
4. Well-Encapsulated Extension

   1. Percolation class adds a custom parameter (percolation probability) (in the Percolation constructor) that isn’t part of the base framework, demonstrating how the API allows extensions.
5. Clear Simulation Rule Implementation

   1. API enables clean implementation of complex simulation rules via applyRules() method
6. Framework integration

   1. Percolation implementation demonstrates seamless integration with the framework’s core features
      1. Example: Using getNeighbors() from Grid and setNextState(state) from Cell, and implementing  applyRules() from Simulation  (which works with the Simulation’s step() method)

### API 2:

## TEAMWORK & COMMUNICATION OVERVIEW

### SIGNIFICANT POSITIVE EVENT:

* Occasionally, a team member would be unable to contribute as much to the project because they were
  either sick, were out of town, or had other academic obligations. Although these challenges could
  have
  set back out timeline, each member of our team was able to pick up extra work to ensure that the
  project
  would be completed on time.
* For example, when Alana had the flu, Tatum and Angela were able to complete more of the design doc
* When Angela had the flu, Tatum and Alana were able to assist with the XML and controller methods
* When Angela and Tatum had midterms, Alana was able to help with debugging portions of code that
  she had not written herself
* Our team never made other teammates feel bad when they couldn't contribute as much as normal, and
  we all supported one another.

### SIGNIFICANT PROBLEM:

* A significant problem our team experienced was surrounding git and merge conflicts
* Merge conflicts were common - we often struggled to resolve them, which would break our main
  branch
* Our team learned that when you experience a merge conflict, you should either text or meet-up with
  your teammates
  to resolve it together
* We also learned the importance of testing within our own branches before pushing to main
  * Everything should be fully functional and all tests should pass before merging to main

### STRONG TEAMWORK/COMMUNICATION:

* Our team worked really well together because we constantly checked in with one another and were
  willing to solve problems for our other teammates rather than focus on our own features.
* For example, Alana would send check-in messages over the weekends to make sure everyone was doing
  ok, both
  personally and within the context of 308.
* Another example was when Angela needed assistance debugging a controller method, Alana helped
  debug the method
  and discovered that a View method was partially causing the problem.
* Another example was when Alana needed back-end assistance to display the simulation appropriately,
  Tatum immediately paused working on another feature to work on Alana's feature
* Additionally, when Tatum needed configuration files to see if her simulation was running, Angela
  paused working
  on her controller portion of the feature requirements and wrote various config files
* This willingness to help one another and selflessness was what made working together so enjoyable

### TEAMWORK/COMMUNICATION AREA FOR IMPROVEMENT:

* One area for improvement would be meeting in-person with our team more frequently
* Our team only met in person approximately once a week or less, but when we did meet in person, we
  found that it was significantly easier to complete the project
* This form of collaboration would have made the project easier, and going forward, our team is
  going to emphasize in-person collaboration
* In general, we could have communicated more with one another, and even tried to integrate a weekly
  stand-up meeting

### Alana Teamwork Improvement

* Worked to communicate and check-in more frequently with her team.
* Sent weekly texts highlighting what she was working on for the next few days and overall
  team goals for the week
* She would communicate when there was a merge conflict and worked harder to not alter her teammates
  code without checking with them first
* Alana consistently informed her team when one of her features wasn't working and always prioritized 
debugging her teammates code before her own
* Alana often went beyond her own responsibilities to help teammates or explain code

#### Alana Evidence To Collect

* Going forward, Alana can read her team feedback form from her teammates to see if she is improving
* Alana can also count the number of merge conflicts she has per week. Merge conflicts typically
  indicate that team members are not communicating what they are working on, so fewer conflicts
  shows better teamwork
* Alana can also analyze how frequently she communicates with her team by counting the number of
  messages sent per week to her team/number of team meetings

### TATUM TEAMWORK IMPROVEMENT:

- Tatum made sure all of her backend features were implemented early on so that files could be made for them and frontend features could be implemented for them without delays.

- Tatum responded regularly in group text about updates and progress to keep everyone informed about her work.

- Tatum asked questions about other people's implementations if she didn't understand them to ensure clear communication across the team.

- Tatum worked to attend all group meetings so she could contribute to discussions and stay aligned with team goals.

- Tatum set aside developing features to help with overall project when needed, prioritizing team success over individual tasks.

- Tatum reached out for others’ opinions on development before developing big aspects to incorporate diverse perspectives into the design.

#### TATUM EVIDENCE TO COLLECT

- Tatum can track her completion rate of tasks to see if it was enough time for teammates to do their part based on her implementation and improve her timing estimates.

- Tatum can request specific feedback on her communication clarity and responsiveness from teammates to identify areas for improvement.

- Tatum can track the number of messages sent to group text each week to ensure consistent communication with her team.

- Tatum can document instances where she identified potential issues before they became problems to demonstrate proactive problem-solving.

- Tatum can measure how quickly teammates were able to build on her backend features to gauge the quality of her documentation and implementation.

- Tatum can count how many clarification questions she needs to answer after delivering features as an indicator of communication effectiveness.

- Tatum can compare estimated vs. actual completion times for her components to improve her planning and estimation skills.

- Tatum can analyze how often her early input prevents later redesigns to quantify the value of her upfront contributions.

### ANGELA TEAMWORK IMPROVEMENT
- Angela can work on branching with git and making sure everything is working perfectly on her personal branch before pushing to main, since controller edits can mess up the entire simulation.

- Angela can communicate better with her teammates about when certain configuration features will be working properly since these can impact the testing of simulations to make sure they are working properly.

- Angela can document the API of the controller classes better so that other teammates can test and call it properly from main methods.

- Angela can communicate when refactoring the controller or configuration classes so that other teammates know to refrain from pushing to avoid merge conflicts.

- Angela can offer to assist other teammates with certain features or  testing or javadoc on weeks when there were less configuration elements to implement.

#### ANGELA EVIDENCE TO COLLECT:

- Angela can track how many messages she sends in our group chat about updating her team members with her progress or known bugs.

- Angela can track how often she pushes to Git as well as which commits are tagged and how many files are edited/lines are edited.

- Angela can track how many of her classes have JUnit tests and how many lines are covered.

- Angela can count how many questions she created for her mentor TA or for the group chat or for office hours.

- Angela can count how many files she edited outside of the controller to account for helping teammates.
