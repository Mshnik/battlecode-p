package analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mshnik
 */
class Status {
  private static final double STARTING_BULLETS = 300;
  private static final int BASE_BULLETS_PER_ROUND = 2;
  private static final double BULLET_PER_ROUND_TAX = 0.01;

  static final int GARDENER_COST = 100;
  static final int TREE_COST = 50;

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

  Action processRound(Action action) {
    double bullets = bulletsByRound.get(round);
    int vp = vpByRound.get(round);
    boolean gardenerBought = false;
    int treesBought = 0;

    // Constant bullet additions
    bullets += Math.max(BASE_BULLETS_PER_ROUND - BULLET_PER_ROUND_TAX * bullets, 0);

    //Inc gardener rounds
    roundsSinceBoughtGardener++;
    for(Gardener g : gardeners) {
      g.incRoundsSinceBoughtTree();
    }

    // Buy gardener if desired and possible
    if (action.shouldBuyGardener() && roundsSinceBoughtGardener >= GARDENER_COOLDOWN && bullets >= GARDENER_COST) {
      bullets -= GARDENER_COST;
      gardeners.add(new Gardener());
      roundsBoughtGardener.add(round);
      roundsSinceBoughtGardener = 0;
      gardenerBought = true;
    }

    // Buy tree(s) if desired and possible
    for(int i = 0; i < action.getTreesToBuy(); i++) {
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
      treesBought++;
    }

    // Convert bullets if desired and possible
    int bulletsToConvert = Math.min(action.getBulletsToConvert(), (int)(bullets/10)*10);
    bullets -= bulletsToConvert;
    vp += bulletsToConvert/10;
    vpByRound.add(vp);

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
    return new Action().withBulletsToConvert(bulletsToConvert).withShouldBuyGardener(gardenerBought).withTreesToBuy(treesBought);
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

  int gardenersSize() {
    return gardeners.size();
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