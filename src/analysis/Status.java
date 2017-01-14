package analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mshnik
 */
class Status {
  enum Result {
    NONE,
    BOUGHT_GARDENER,
    BOUGHT_TREE,
    VICTORY
  }

  private static final double STARTING_BULLETS = 300;
  private static final int BASE_BULLETS_PER_ROUND = 2;
  private static final double BULLET_PER_ROUND_TAX = 0.01;

  private static final int GARDENER_COST = 100;
  private static final int TREE_COST = 50;

  private static final int GARDENER_COOLDOWN = 20;

  private int round;
  private int roundsSinceBoughtGardener;
  private List<Double> bulletsByRound;
  private List<Integer> vpByRound;
  private List<Gardener> gardeners;
  private List<Tree> trees;

  private List<Integer> roundsBoughtGardener;
  private List<Integer> roundsBoughtTree;

  Status() {
    round = 0;
    roundsSinceBoughtGardener = GARDENER_COOLDOWN;
    bulletsByRound = new ArrayList<>();
    vpByRound = new ArrayList<>();
    bulletsByRound.add(STARTING_BULLETS);
    vpByRound.add(0);
    gardeners = new ArrayList<>();
    trees = new ArrayList<>();
    roundsBoughtGardener = new ArrayList<>();
    roundsBoughtTree = new ArrayList<>();
  }

  Result processRound(Action action, int bulletsToConvert) {
    double bullets = bulletsByRound.get(round);
    int vp = vpByRound.get(round);

    // Constant bullet additions
    bullets += Math.max(BASE_BULLETS_PER_ROUND - BULLET_PER_ROUND_TAX * bullets, 0);

    bulletsToConvert = Math.min(bulletsToConvert, (int)(bullets/10)*10);
    bullets -= bulletsToConvert;
    vp += bulletsToConvert/10;
    vpByRound.add(vp);

    //Inc gardener rounds
    roundsSinceBoughtGardener++;
    for(Gardener g : gardeners) {
      g.incRoundsSinceBoughtTree();
    }

    Result r = Result.NONE;

    switch (action.type) {
      case NOTHING:
        break;
      case BUY_GARDENER:
        if (roundsSinceBoughtGardener < GARDENER_COOLDOWN) {
          break;
        }
        if (bullets < GARDENER_COST) {
          break;
        }
        bullets -= GARDENER_COST;
        gardeners.add(new Gardener());
        roundsBoughtGardener.add(round);
        roundsSinceBoughtGardener = 0;
        r = Result.BOUGHT_GARDENER;
        break;
      case BUY_TREE:
        if (gardeners.isEmpty()) {
          throw new RuntimeException("Illegal Action on round " + round + ", can't buy tree without gardener");
        }
        Gardener gardenerToPlant = getGardenerThatCanPlant();
        if (gardenerToPlant == null) {
          break;
        }
        if (bullets < TREE_COST) {
          break;
        }
        gardenerToPlant.resetRoundsSinceBoughtTree();
        roundsBoughtTree.add(round);
        bullets -= TREE_COST;
        trees.add(new Tree());
        r = Result.BOUGHT_TREE;
        break;
    }

    if (trees.size() > 0) {
      List<Tree> treesToWater = selectTreesToWater();

      for (Tree t : treesToWater) {
        bullets += t.incRoundAndGetOutput(true);
      }
      for (Tree t : trees) {
        bullets += t.incRoundAndGetOutput(false);
      }
      trees.addAll(treesToWater);
    } else {
      for (Tree t : trees) {
        bullets += t.incRoundAndGetOutput(false);
      }
    }
    bulletsByRound.add(bullets);
    round++;
    return r;
  }

  private List<Tree> selectTreesToWater() {
    int i = 0;
    List<Tree> treesToWater = new ArrayList<>();
    while (i < trees.size() && treesToWater.size() < gardeners.size()) {
      Tree tree = trees.remove(0);
      if (tree.isMature()) {
        treesToWater.add(tree);
      } else {
        trees.add(tree);
      }
      i++;
    }
    return treesToWater;
  }

  private Gardener getGardenerThatCanPlant() {
    for(Gardener g : gardeners) {
      if (g.canBuyTree()) {
        return g;
      }
    }
    return null;
  }

  int getRound() {
    return round;
  }

  int treesSize() {
    return trees.size();
  }

  double getBullets() {
    return bulletsByRound.get(round);
  }

  int getVp() {
    return vpByRound.get(round);
  }

  List<Double> getBulletsByRound() {
    return bulletsByRound;
  }

  List<Integer> getVpByRound() {
    return vpByRound;
  }

  public String byRoundString() {
    StringBuilder sb = new StringBuilder("[\n");
    for(int i = 0; i < round; i++) {
      sb.append('\t');
      sb.append(vpByRound.get(i));
      sb.append(",\t");
      sb.append(bulletsByRound.get(i));
      if (roundsBoughtGardener.contains(i)) {
        sb.append(",\t");
        sb.append("Bought Gardener");
      }
      if (roundsBoughtTree.contains(i)) {
        sb.append(",\t");
        sb.append("Bought Tree");
      }
      sb.append('\n');
    }
    sb.append("]");
    return sb.toString();
  }
}