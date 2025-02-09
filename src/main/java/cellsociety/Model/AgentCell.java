package cellsociety.Model;

/**
 * A cell specialized for Schelling's simulation that holds an agent group identifier.
 */
public class AgentCell extends Cell {

  private int agentGroup;

  /**
   * Constructs an {@code AgentCell} with the specified state and agent group.
   *
   * The constructor initializes an agent cell with the given state and agent group. It ensures that the provided
   * agent group is non-negative. If the provided agent group is negative, an {@link IllegalArgumentException} is thrown.
   *
   * @param state the state of the agent cell (must not be {@code null})
   * @param agentGroup the agent group to be assigned to the agent cell (must not be negative)
   * @throws IllegalArgumentException if {@code agentGroup} is less than 0
   */
  public AgentCell(StateInterface state, int agentGroup) {
    super(state);
    if (agentGroup < 0) {
      throw new IllegalArgumentException("Agent group cannot be negative.");
    }
    this.agentGroup = agentGroup;
  }

  /**
   * Returns the agent's group identifier.
   *
   * @return the group identifier
   */
  public int getAgentGroup() {
    return agentGroup;
  }

  /**
   * Sets the agent group to the specified value.
   *
   * This method sets the agent group for the agent, ensuring that the specified group number is non-negative.
   * If the provided agent group is negative, an {@link IllegalArgumentException} is thrown.
   *
   * @param agentGroup the agent group to be set (must not be negative)
   * @throws IllegalArgumentException if {@code agentGroup} is less than 0
   */
  public void setAgentGroup(int agentGroup) {
    if (agentGroup < 0) {
      throw new IllegalArgumentException("Agent group cannot be negative.");
    }
    this.agentGroup = agentGroup;
  }
}
