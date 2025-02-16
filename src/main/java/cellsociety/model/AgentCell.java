package cellsociety.model;

/**
 * A cell specialized for Schelling's simulation that holds a mutable agent group identifier.
 * Validates state and agent group parameters during construction and modification.
 * @author Tatum McKinnis
 */
public class AgentCell extends Cell {

  private static final String INVALID_AGENT_GROUP_MSG = "Agent group cannot be negative. Received: %d";
  private static final String NULL_STATE_MSG = "State cannot be null";

  private int agentGroup;

  /**
   * Constructs an {@code AgentCell} with the specified state and agent group identifier. Validates
   * parameters before initialization using class validation methods.
   *
   * @param state      the cellular state object (must implement {@link StateInterface})
   * @param agentGroup the affiliation group identifier for the agent
   * @throws IllegalArgumentException if state is null or agentGroup is negative
   */
  public AgentCell(StateInterface state, int agentGroup) {
    super(validateState(state));
    validateAgentGroup(agentGroup);
    this.agentGroup = agentGroup;
  }

  /**
   * Validates the state parameter before passing to superclass constructor.
   *
   * @param state the state object to validate
   * @return the validated state object
   * @throws IllegalArgumentException if state is null
   */
  private static StateInterface validateState(StateInterface state) {
    if (state == null) {
      throw new IllegalArgumentException(NULL_STATE_MSG);
    }
    return state;
  }

  /**
   * Validates agent group identifier meets simulation requirements.
   *
   * @param agentGroup the group identifier to validate
   * @throws IllegalArgumentException if agentGroup is negative
   */
  private void validateAgentGroup(int agentGroup) {
    if (agentGroup < 0) {
      throw new IllegalArgumentException(
          String.format(INVALID_AGENT_GROUP_MSG, agentGroup)
      );
    }
  }

  /**
   * Retrieves the current agent group identifier.
   *
   * @return the agent's current group affiliation identifier
   */
  public int getAgentGroup() {
    return agentGroup;
  }

  /**
   * Updates the agent's group identifier with validation.
   *
   * @param agentGroup the new group identifier to assign
   * @throws IllegalArgumentException if agentGroup is negative
   */
  public void setAgentGroup(int agentGroup) {
    validateAgentGroup(agentGroup);
    this.agentGroup = agentGroup;
  }
}