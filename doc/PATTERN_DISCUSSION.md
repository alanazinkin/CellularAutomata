# Lab Java Design Pattern Discussion
### Alana Zinkin, Angela Predolac, Tatum Mckinnis


## Iterator

* Problem solved

* Trade-offs

* Use in Java vs description


## Factory (static methods)

* Problem solved

* Trade-offs

* Use in Java vs description


## Factory

* Problem solved: allows users to create a general type of object for different cases
without having to specifying the exact constructor. Removes lots of extra switch statements
* and allows for more code flexibility

* Trade-offs: The factory template requires that all the classes extend the original class,
which reduces the flexibility of the design pattern. The code can become overly complicated as well.

* Use in Java vs description: The factory design pattern separates the product construction
code from the code that actually uses the product, which makes it easier to extend the code base later on
without making drastic changes.


## Strategy

* Problem solved: you have a very specific task that can be done various different ways,
so you want different algorithms to do the task.

* Trade-offs: If you only have a couple of algorithms and they rarely change, 
there’s no real reason to overcomplicate the program.
* Users should know about the different strategies in order to pick the one they want to use

* Use in Java vs description: Routing app that has different ways of getting around, like a biking, 
driving, or walking route. Each of these means of getting to the destination has different methods.


## Template Method

* Problem solved

* Trade-offs

* Use in Java vs description


## Builder

* Problem solved

* Trade-offs

* Use in Java vs description


## Observer

* Problem solved

* Trade-offs

* Use in Java vs description


## Decorator

* Problem solved

* Trade-offs

* Use in Java vs description




## Discussion

* Which patterns' use seem the most different from its general description?

* Which patterns do you think you are already using?
  * We area already using the factory design pattern and we might consider using the strategy design pattern going forward

* Which patterns seem the most useful to use in the SLogo project?

* What did you learn about design while studying these patterns?
