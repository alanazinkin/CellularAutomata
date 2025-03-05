## DESIGN OVERVIEW

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

## TESTING OVERVIEW

#### TEST 1:

#### TEST 2:

#### TEST 3:

#### TEST 4:

#### TEST 5:

#### TEST 6:

#### TESTFX TEST 1:

```angular2html
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

```angular2html
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

```angular2html
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

#### Alana Evidence To Collect

* Going forward, Alana can read her team feedback form from her teammates to see if she is improving
* Alana can also count the number of merge conflicts she has per week. Merge conflicts typically
  indicate that team members are not communicating what they are working on, so fewer conflicts
  shows better teamwork
* Alana can also analyze how frequently she communicates with her team by counting the number of
  messages sent per week to her team/number of team meetings

### TATUM TEAMWORK IMPROVEMENT:

#### TATUM EVIDENCE TO COLLECT

### ANGELA TEAMWORK IMPROVEMENT:

#### ANGELA EVIDENCE TO COLLECT:
