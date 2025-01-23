# Breakout Abstractions Lab Discussion

#### NAMEs: Tatum McKinnis, Angela Predolac, Alana Zinkin

### Block

This superclass's purpose as an abstraction:

public abstract class Block {
public Color getColor ()

public void handleHit()

public Rectangle getBlockObject()
}


#### Subclasses

Each subclass's high-level behavorial differences from the superclass

MultiHit Block - Not automatically destroyed in handleHit() just decrease number of hits. 

PowerUp Block - Signals the player has received a PowerUp in handleHit()

#### Affect on Game/Level class

Which methods are simplified by using this abstraction and why

When handling a block collision, the generic block will handle the hit based on what type of block it actually is rather than having to use multiple if statements to check for the type of block. Also, when generating the grid of blocks, you can instantiate the different block classes rather than one general block class and then having to check for different conditions to specify the type. 

### Power-up

This superclass's purpose as an abstraction:

```java

 public abstract class PowerUp {
     public void activate() 
  
 }

```

#### Subclasses

Each subclass's high-level behavorial differences from the superclass

Laser - A laser coming out of the paddle. When activated, a laser will shoot out of the paddle and be able to destroy blocks. 

Extra Balls - The player shoots multiple balls at once. When activated, the player will be able to control the direction of multiple balls and be able to destroy more blocks. 

Shield - When ball goes about of bounds, the player does not lose a life right away. When activated, the player's current life amount will be frozen. 

#### Affect on Game/Level class

Which methods are simplified by using this abstraction and why

This simplifies block hits for power up blocks because we do not have to include different if statements for each power up we can just call the activate method from the specific power up class. 

```

```
