package analysis;

/**
 * @author Mshnik
 */
class Tree {
  private static final int STARTING_HEALTH = 10;
  static final int MAX_HEALTH = 50;
  private static final double MATURE_HEALTH_INC = 0.5;
  static final double WITHER_RATIO = 0.01;
  static final int WATER_HEAL_AMOUNT = 5;
  private static final double PRODUCTION_RATIO = 0.02;
  private static final int MATURATION_ROUNDS = 80;

  private int round;
  private double health;
  private boolean alive;

  Tree() {
    health = STARTING_HEALTH;
    round = 0;
    alive = true;
  }

  boolean isMature() {
    return round > MATURATION_ROUNDS;
  }

  double incRoundAndGetOutput(boolean isWatered) {
    round++;
    if (alive) {
      if (round > MATURATION_ROUNDS) {
        double payoff = health * PRODUCTION_RATIO;
        health = health - MAX_HEALTH * WITHER_RATIO;
        if (isWatered) {
          health = Math.min(MAX_HEALTH, health + WATER_HEAL_AMOUNT);
        }
        if (health <= 0) {
          alive = false;
        }
        return payoff;
      } else {
        health += MATURE_HEALTH_INC;
        return 0;
      }
    } else {
      return 0;
    }
  }
}
