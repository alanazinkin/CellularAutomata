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

## Use Cases

## Design Considerations

## Team Responsibilities

* Team Member #1
* Team Member #2
* Team Member #3
