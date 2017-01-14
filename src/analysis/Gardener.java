package analysis;

/**
 * @author Mshnik
 */
class Gardener {
  private static int TREE_COOLDOWN = 10;

  private int roundsSinceBoughtTree;

  Gardener() {
    roundsSinceBoughtTree = TREE_COOLDOWN;
  }

  boolean canBuyTree() {
    return roundsSinceBoughtTree >= TREE_COOLDOWN;
  }

  void incRoundsSinceBoughtTree() {
    roundsSinceBoughtTree++;
  }

  void resetRoundsSinceBoughtTree() {
    roundsSinceBoughtTree = 0;
  }
}
