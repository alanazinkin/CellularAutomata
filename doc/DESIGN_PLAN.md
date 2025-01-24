# Cell Society Design Plan

### Team Number: 05

### Names: Tatum McKinnis, Angela Predolac, Alana Zinkin

#### Examples

Here is a graphical look at my design:

![This is cool, too bad you can't see it](images/online-shopping-uml-example.png "An initial UI")

made from [a tool that generates UML from existing code](http://staruml.io/).

Here is our amazing UI:

![This is cool, too bad you can't see it](images/29-sketched-ui-wireframe.jpg "An alternate design")

taken from [Brilliant Examples of Sketched UI Wireframes and Mock-Ups](https://onextrapixel.com/40-brilliant-examples-of-sketched-ui-wireframes-and-mock-ups/).

## Overview

The primary problem this program aims to solve is the lack of intuitive, flexible, and extensible tools for simulating and visualizing Cellular Automata (CA) models. Cellular Automata are powerful for studying complex systems, modeling natural phenomena, and exploring mathematical concepts. However, existing tools often fall short in modularity, customization, and user-friendliness, making it difficult for users to experiment with different CA models or extend the platform for new use cases. This program addresses these challenges by focusing on three key design goals: flexibility, user-friendliness, and modularity.

Flexibility is achieved by allowing users to define rules, parameters, and grid structures for new CA models through external configuration files, eliminating the need to modify the program's core logic. The user interface is designed to be clean and simple, enabling users to load simulations, adjust parameters like speed, and observe grid evolution in real time. Additionally, the program provides clear feedback for errors, such as invalid input files or unsupported grid formats, ensuring a seamless user experience.

The architecture of the program emphasizes modularity and extensibility by adhering to the principle of being "closed for modification, open for extension." Core components such as grid manipulation, simulation rules, and rendering are designed to be stable and unchanging, while extension points are provided through well-defined abstractions and interfaces. This allows developers to add new CA models, file configurations, or display formats without altering existing code.

At a high level, the program creates a robust and adaptable framework for simulating a variety of CA models, including Conway's Game of Life, Spreading of Fire, Schelling's Model of Segregation, and Wa-Tor World. The design separates concerns into modular components—such as grid management, rule execution, and user interaction—ensuring that each part of the program can be developed, tested, and extended independently while maintaining seamless integration and scalability.By focusing on high-level abstractions, this program is designed to be both versatile and user-friendly, catering to a wide range of users.

## User Interface

## Configuration File Format

## Design Details

Model-View-Controller Separation:

* The Model will be agnostic of the UI and will contain logic for updating cell states based on the simulation rules. The model will handle the grid structure and its evolution, but it will not concern itself with how the data is displayed to the user.
* The View will be responsible for presenting the grid and simulation data. It will use JavaFX to render the grid and allow user interactions. It will listen for changes in the model and update the grid display accordingly.
* The Controller will act as an intermediary, interpreting user actions and invoking corresponding methods on the model to update the state, and passing necessary data to the view.

Model Classes (handles simulation logic and data):

Simulation: Abstract base class for all simulation types (Game of Life, Spreading Fire, etc.) defining common methods like step(), initialize(), etc. Specific implementations could be GameOfLifeSimulation, FireSimulation, etc.

- Encapsulates simulation's rules, state transitions, and grid initialization. Works with Grid and Cell to update the simulation state.

Cell: Represents each cell on the grid. Stores the state (alive, dead, burning, etc.) and possibly the row and column index as well as it computes the next state based on simulation rules and manages its transitions. Can have subclasses of different types of cells.

- Works with Grid and is updated by Simulation.

Grid: Represents the grid of cells. Manages a 2D array (or List of Lists) of Cell objects, holds grid size, and manages cell interactions based on the Simulation type.

- Manages grid dimensions, neighbor computations, and state updates for cells. Interacts with Cell to retrieve and update individual states.

State: Could use enums (or a constant class) for cell states (ALIVE, DEAD, BURNING, etc.) and this class could define state types.

XML Parser: Read and parse the configuration XML file, extracting details like grid size, initial states, and simulation parameters.

View Classes (User interface classes):

SimulationView: Renders simulation state, updates display, and interacts with user actions

GridView: Renders the grid of cells on the screen (should visually update when state of cells change)

SimulationInfoPanel: Shows information like simulation title, author, description, parameters, etc.

ControlPanel: Contains buttons for starting, pausing, and stopping the simulation. Includes sliders for speed control, a button for loading new simulations, etc.

Controller Classes (Handle user interactions):

SimulationController: Handles the start/pause/stop simulation actions. Listens for user interactions, like button presses, and interacts with Simulation model to run the specific Simulation.

Maybe a GridController if Grid needs direct control (resetting or manipulating individual cells)

Other helper/support classes:

SimulationParameter: Holds specific parameters required for a specific simulation. This would centralize the management of parameters and pass them to the corresponding simulation class. Different simulations need different paramters that control the behavior of the simulation, like rate of fire spread, birth and death probabilities, etc. These parameters will influence how the simulation spreads over time.

Game flow: User input flows from SimulationView to Simulation, triggering updates in the model. State updates are then computed in Simulation and propagate to Grid and Cell. Output displayed via SimulationView by rendering the updated Grid. 

## Use Cases
Use Case 1: Apply the rules to a middle cell: set the next state of a cell to dead by counting its number of neighbors using the Game of Life rules for a cell in the middle (i.e., with all its neighbors)
 * Within step() of the Simulation class, call updateState(cell) of the Grid class, which calls the countAliveNeighbors() method of Grid class, the getState(cell) method of the Cell class to check if it's alive or dead. In our use case, the cell would be dead.
   and we call setNextState(cell) of the Cell class according to the game rules in the Simulation class which uses the return value of the countAliveNeighbors() method.
Use Case 2:
## Design Considerations

## Team Responsibilities

* Team Member #1
* Team Member #2
* Team Member #3
