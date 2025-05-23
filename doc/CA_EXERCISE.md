```
# Cell Society Design Lab Discussion
#### Tatum McKinnis, Angela Predolac, Alana Zinkin


### Simulations

 * Commonalities
In Conway's Game of Life, whether the cell lives or dies depends on the surrounding cells and in the Spreading of Fire simulation the tree cell burns based on whether its neighbor is burning. So, the state of the cell in both simulations depends on the state of its neighboring cells. 
 
 * Variations
Conway's Game of Life simulation does not require any input while the Spreading of Fire simulation does require input (the probability p that the tree ignites). 


### Discussion Questions

 * How does a Cell know what rules to apply for its simulation?

A Cell can know what rules to apply for its simulation based on which Simulation class is being used. Each Simulation class will contain rules for that specific Simulation that decide whether a Cell lives or dies. 
 * How does a Cell know about its neighbors?

A Cell can know about its neighbors by storing its current neighbors in a list which will hold four or two other cells depending on the cell's position in the Grid. 
 * How can a Cell update itself without affecting its neighbors update?


 * What behaviors does the Grid itself have?
The Grid would store a list of cells. 

 * How can a Grid update all the Cells it contains?

 * What information about a simulation needs to be in the configuration file?

 * How is configuration information used to set up a simulation?

 * How is the graphical view of the simulation updated after all the cells have been updated?



### Alternate Designs

#### Design Idea #1

 * Data Structure #1 and File Format #1

 * Data Structure #2 and File Format #2


#### Design Idea #2

 * Data Structure #1 and File Format #1

 * Data Structure #2 and File Format #2
 
 

### High Level Design Goals



### CRC Card Classes

This class's purpose or value is to represent a customer's order:
![Order Class CRC Card](images/order_crc_card.png "Order Class")


This class's purpose or value is to represent a customer's order:

|Order| |
|---|---|
|boolean isInStock(OrderLine)         |OrderLine|
|double getTotalPrice(OrderLine)      |Customer|
|boolean isValidPayment (Customer)    | |
|void deliverTo (OrderLine, Customer) | |


This class's purpose or value is to represent a customer's order:
```java
public class Order {
     // returns whether or not the given items are available to order
     public boolean isInStock (OrderLine items)
     // sums the price of all the given items
     public double getTotalPrice (OrderLine items)
     // returns whether or not the customer's payment is valid
     public boolean isValidPayment (Customer customer)
     // dispatches the items to be ordered to the customer's selected address
     public void deliverTo (OrderLine items, Customer customer)
 }
```

This class's purpose or value is to manage something:

```java
public class Something {
     // sums the numbers in the given data
     public int getTotal (Collection<Integer> data)
	 // creates an order from the given data
     public Order makeOrder (String structuredData)
 }
```

### Use Cases

* Apply the rules to a middle cell: set the next state of a cell to dead by counting its number of neighbors using the Game of Life rules for a cell in the middle (i.e., with all its neighbors)

```java
 Something thing = new Something();
 Order o = thing.makeOrder("coffee,large,black");
 o.update(13);
```

* Apply the rules to an edge cell: set the next state of a cell to live by counting its number of neighbors using the Game of Life rules for a cell on the edge (i.e., with some of its neighbors missing)

```java
 Something thing = new Something();
 Order o = thing.makeOrder("coffee,large,black");
 o.update(13);
```

* Move to the next generation: update all cells in a simulation from their current state to their next state and display the result graphically

```java
 Something thing = new Something();
 Order o = thing.makeOrder("coffee,large,black");
 o.update(13);
```

* Set a simulation parameter: set the value of a parameter, probCatch, for a simulation, Fire, based on the value given in a data file

```java
 Something thing = new Something();
 Order o = thing.makeOrder("coffee,large,black");
 o.update(13);
```

* Switch simulations: load a new simulation from a data file, replacing the current running simulation with the newly loaded one

```java
 Something thing = new Something();
 Order o = thing.makeOrder("coffee,large,black");
 o.update(13);
```

```

```
