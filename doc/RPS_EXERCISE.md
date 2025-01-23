# Rock Paper Scissors Lab Discussion
#### Names and NetIDs


### High Level Design Goals



### CRC Card Classes

This class's purpose is to represent and handle the actual playing of the RPS game:

| Game                                         |      |
|----------------------------------------------|------|
| void startGame()                             |      |
| void endGame()                               |      |
| Player handleTurn(Move p1, Move p2, Move p3) | Move |
| void updateScore()                           |      |
| Player announceWinner()                      |
|                                              |      |

This class's purpose is to represent a general move:

| Move                                         |      |
|----------------------------------------------|------|
| Move getMove()                               |      |
| int compareTo(Move m2)                       | Move |

This class's purpose is to represent a single round of RPS:

| Round                                                |      |
|------------------------------------------------------|------|
| int getRound()                                       |      |
| Player getWinner(Move move1, Move move2, Move move3) | Move |

This class's purpose is to represent a player of the game:

| Player         |       |
|----------------|-------|
| Move getMove() |       |
| int getScore() |       |

This class's purpose is to represent and handle the actual playing of the RPS game:

```java
public class Game {
     // starts the game
     public void startGame()
    // determines when game is over and stops it
     public void endGame()
    // returns Player who won a single turn
     public Player handleTurn(Move p1, Move p2, Move p3)
     // updates the score after a turn
     public void updateScore()
    // returns the overall winner of the game over multiple rounds
    public Player announceWinner()
}
 ```

This class's purpose is to represent a general move:

```java
public class Move {
     // returns the move
     public Move getMove()
    // returns 0, 1, or -1 to compare to another move
    public int compareTo(Move m2)
}
 ```

This class's purpose is to represent a single round of RPS:

```java
public class Round {
     // returns the round number
     public int getRound()
    // returns player who won this round
    public Player getWinner(Move move1, Move move2, Move move3)
}
 ```

This class's purpose is to represent a player of the game:

```java
public class Player {
     // returns the player's move
     public Move getMove()
    // returns this player's score
    public int getScore()
}
 ```


### Use Cases

* A new game is started.
 ```java
 Game game = new Game();
game.startGame();
 ```

* A move is created to represent rock.
 ```java
Move move = new Move(Move.Rock);
move.getMove();
move.compareTo(Move scissors)
 ```

* In the second round, the winner of this round is determined and sent back to the game.
 ```java
Round round2 = new Round(2);
round.getRound();
round.getWinner();
 ```

* A new player is created with no initial move and a score of 0.
 ```java
Player p = new Player();
p.getMove();
p.getScore();
 ```