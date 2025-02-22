package cellsociety.model;

import java.util.Random;

public class RulesOperations {

  /**
   * Determines if two agents can reproduce.
   * They must both be fertile, have different sexes, and possess enough sugar.
   */
  public static boolean canReproduce(Agent agent1, Agent agent2) {
    return agent1.isFertile() &&
        agent2.isFertile() &&
        agent1.getSex() != agent2.getSex() &&
        agent1.getSugar() >= agent1.getInitialEndowment() &&
        agent2.getSugar() >= agent2.getInitialEndowment();
  }

  /**
   * Reproduces two parent agents to create a new child agent.
   * The child's sugar is based on the average of the parents' initial endowments,
   * and its vision and metabolism are inherited by averaging.
   * The parents lose half of their initial endowment.
   *
   * @param parent1 one parent agent.
   * @param parent2 the other parent agent.
   * @param position the cell where the child will be placed.
   * @param random a Random instance for generating random attributes.
   * @return the newly created child agent.
   */
  public static Agent reproduce(Agent parent1, Agent parent2, Cell position, Random random) {
    int childSugar = (parent1.getInitialEndowment() + parent2.getInitialEndowment()) / 2;
    parent1.removeSugar(parent1.getInitialEndowment() / 2);
    parent2.removeSugar(parent2.getInitialEndowment() / 2);
    int childVision = inheritVision(parent1, parent2);
    int childMetabolism = inheritMetabolism(parent1, parent2);
    Agent child = new Agent(position, childSugar, childVision, childMetabolism);
    // Randomly choose one parent's sex for the child; alternatively, you might choose the opposite of one parent
    child.setSex(random.nextBoolean() ? parent1.getSex() : parent2.getSex());
    child.setFertile(false);  // Newborn agents start infertile
    return child;
  }

  /**
   * Helper method to inherit vision by averaging parents' vision.
   */
  private static int inheritVision(Agent parent1, Agent parent2) {
    return (parent1.getVision() + parent2.getVision()) / 2;
  }

  /**
   * Helper method to inherit metabolism by averaging parents' metabolism.
   */
  private static int inheritMetabolism(Agent parent1, Agent parent2) {
    return (parent1.getMetabolism() + parent2.getMetabolism()) / 2;
  }

  /**
   * Determines whether two agents can trade based on their marginal rates of substitution.
   */
  public static boolean canTrade(Agent agent1, Agent agent2) {
    double mrs1 = agent1.getMarginalRateOfSubstitution();
    double mrs2 = agent2.getMarginalRateOfSubstitution();
    return mrs1 != mrs2 && Math.abs(mrs1 - mrs2) > 0.1;
  }

  /**
   * Executes a trade between two agents based on the geometric mean of their marginal rates of substitution.
   */
  public static void executeTrade(Agent agent1, Agent agent2) {
    double p = Math.sqrt(agent1.getMarginalRateOfSubstitution() * agent2.getMarginalRateOfSubstitution());
    if (p >= 1) {
      agent1.tradeSugarForSpice(agent2, 1, p);
    } else {
      agent1.tradeSpiceForSugar(agent2, 1, 1 / p);
    }
  }
}

