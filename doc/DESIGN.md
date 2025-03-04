# Cell Society Design Final
### 5
### Alana Zinkin, Tatum McKinnis, Angela Predolac


## Team Roles and Responsibilities

 * Team Member #1: Alana Zinkin
   * Alana was responsible for the View portion of the simulation, which includes the
   front-end design, CSS styling, and language options.
   * She was also responsible for GUI testing, and she worked on various simulation controller
   methods alongside Angela, such as the starting/stopping/resetting the simulation and allowing
   the user to dynamically adjust the cell shape, grid tiling, edge type, and neighborhood arrangement.
   * Alana also contributed various XML files for the simulations and helped integrate the front-end and 
   back-end for each new simulation.

 * Team Member #2: Tatum McKinnis

 * Team Member #3: Angela Predolac



## Design goals
Our team's major design goals were to create flexible classes, methods, and easy to read code, such that future
development would be simple and straight-forward. Our main goal was to ensure that new simulations
could be easily added. We sought to adhere to SOLID design principles, include the Open-Closed principle,
Single-Responsibility principle, and reducing code redundancies. Our team wanted to make our code as clear and readable as possible
by using informative names and design patterns to ensure that future developers would be able to understand our classes and methods.
Another important design goal was to encapsulate our methods appropriately and reduce the number of public methods.
We wanted to ensure that our API made sense and could be used by other teams if necessary.
We made significant refactoring efforts to ensure that our classes and methods were relatively short. For example,
SimulationController was broken down into over 20 different classes. We also made an effort to add JavaDoc comments
to all public and protected methods and classes to ensure that other developers would be able to understand our API.
Finally, we sh=ought to use common design patterns like Strategy and Factory to streamline development and allow for more flexibility.
#### What Features are Easy to Add
#### Model:
* 

#### View:
* It was relatively simple to add new cell shapes and grid views since I made abstract classes
for each and was able to simply extend the original class.
* It was also relatively simple to add front-end components such as the control bars, buttons, and combo-boxes.
* I found it surprisingly simple to make a new simulation. However, upon further inspection, this made sense given our methods were well-encapsulated
* It was simple to integrate the front-end and the back-end of each new simulation, which means that future development should be simple

#### Controller:
*

## High-level Design
* Our project is broken down into a Model, View, and Controller, and Data folder.
  * The model is responsible for the simulation logic and back-end 
  * The view is responsible for the front-end visual display of the simulation and styling
  * The data folder contains XML configuration files for each simulation in a standard format to allow
  users to easily create new configurations.
  * The controller is responsible for integrating the model, view, and XML configuration files
  into a fully-functional program.

#### Core Classes


## Assumptions that Affect the Design

#### Features Affected by Assumptions


## Significant differences from Original Plan


## New Features HowTo
**App Preferences:** Although we never implemented app preferences, it is relatively straight-forward to do:
1. Create an XML file called "AppPreferences.xml" with tags such as <theme>, <language>, etc.
2. Create a Preferences XML parser modeled off of our XML Parser that looks for the theme tag and language tag
3. Save it in a preferences map of <key, value> pairs with the keys as the type of preference (theme, language, etc.) and the values as the specific preferences
for a user.
4. Create a front-end component called "Set Preferences" that opens a dialog similar to the Grid Settings and allows the user to select their default
preferences from combo-boxes.
5. When the user presses the "Enter" button, a call should be made to the simulation controller, which makes a call to the XML
Writer class. This class will overwrite the values of the "AppPreferences.xml" file to the new preferences that were selected
in the ComboBoxes (if they were not null). This can be done similar to how a configuration file is saved by a user.
6. When the simulation is started, a splash screen appears with language drop down combo boxes and theme combo boxes. We can set the default values in
these combo boxes to be pulled from the app preferences map so that the user does not need to update them each time, but has the flexibility
to change them if desired.

**Help Documentation**: Help documentation is relatively simple to implement

**Pentagonal Tiling**: 
1. Create a new PentagonGridViewFactory class, PentagonGridView class, PentagonCell, and PentagonCellFactory
2. Construct the classes similar to the Hexagon classes. The main difference will be how the setDimensions method is overridden
3. 
#### Easy to Add Features

#### Other Features not yet Done

