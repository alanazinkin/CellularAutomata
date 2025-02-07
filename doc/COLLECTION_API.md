# Collections API Lab Discussion
### NAMES: Angela Predolac, Alana Zinkin, Tatum McKinnis
### TEAM: #5



## In your experience using these collections, are they hard or easy to use?

In our experiences, these collections are very easy to use and accessible. They reduce programming effort, including effort to learn new APIs and design new APIs. We have all depended on the different kinds of collections like Lists and Sets frequently in our coding experiences.

## In your experience using these collections, do you feel mistakes are easy to avoid?

Yes, we feel that mistakes are easy to avoid, because the methods are well-named regardless of the implementation. For example, add() always adds an extra parameter regardless of the type of collection.

## What methods are common to all collections (except Maps)?

Some methods common to all collections except Maps are contains(), add(), and remove().

## What methods are common to all Deques?

The methods for insert, remove, and examine are common to all Deques.

## What is the purpose of each interface implemented by LinkedList?

There are 5 different general purpose implementations that all implement 2 interfaces. For LinkedList, there are lists and deques. The purpose of having these interfaces is to store different representations of data (there is some kind of order in a deque).

## How many different implementations are there for a Set?

Sets have 3 different implementations (HashSet, TreeSet, LinkedHashSet). Lists have 2: ArrayList and LinkedList. Queue and Deque have 2: ArrayDeque and LinkedList. Finally, Maps have 3: HashMap, TreeMap, and LinkedHashMap. Yes, the number of implementations justifies it being an interface because it is an abstract thing that still needs to be implemented based on the specific needs, which also come with different usages and performance tradeoffs.

## What is the purpose of each superclass of PriorityQueue?

There are 3 superclasses of PriorityQueue: AbstractQueue, AbstractCollection, and Object. The purpose of these is to have a basic queue that does not necessarily have priority, collection is to have a basic collection of things of the same type, and object is the root class fir everything.

## What is the purpose of the Collections utility class?

The purpose of the Collections utility class is because there are certain overlapping methods that can be applied to all collections of things: adding another object, removing an object, checking if an object is within the collection. There is no concrete difference, adding List.sort() was just a newer implementation as opposed to the static method Collections.sort(List list).


## API Characterics applied to Collections API

* Easy to learn

* Encourages extension

* Leads to readable code

* Hard to misuse