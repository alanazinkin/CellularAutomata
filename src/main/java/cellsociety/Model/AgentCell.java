package cellsociety.Model;

/**
 * A cell specialized for Schelling's simulation that holds an agent group identifier.
 */
public class AgentCell extends Cell {

  private int agentGroup;

  /**
   * Constructs an AgentCell with the given state and group.
   *
   * @param state the initial state (should be SchellingState.AGENT or SchellingState.EMPTY_CELL)
   * @param agentGroup the group identifier for the agent (ignored if the state is EMPTY_CELL)
   */
  public AgentCell(StateInterface state, int agentGroup) {
    super(state);
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
   * Sets the agent's group identifier.
   *
   * @param agentGroup the new group identifier
   */
  public void setAgentGroup(int agentGroup) {
    this.agentGroup = agentGroup;
  }
}
