package analysis;


import java.io.*;

/**
 * @author Mshnik
 */
class Main {

  private static final int TREES_PER_GARDENER = 6;
  private static final int VICTORY_CONDITION = 1000;
  private static final int MAX_LENGTH = 3000;

  private static class Best {
    int winRound;
    int trees;
    int treesBeforeGardener;

    public String toString() {
      return "Win on round " + winRound + " with " + trees + " trees and " + treesBeforeGardener + " before each gardener";
    }
  }

  public static void main(String[] args) {
    int maxTrees = 100;

    Best best = new Best();
    best.winRound = MAX_LENGTH+1;

    for (int i = 0; i < maxTrees; i++) {
      for(int c = 0; c <= TREES_PER_GARDENER; c++) {
        //System.out.println(String.format(i + "," + getVictoryLength(i)));
        int victoryRound = compute(i, c);
        if (victoryRound != -1 && victoryRound <= best.winRound) {
          best.winRound = victoryRound;
          best.trees = i;
          best.treesBeforeGardener = c;
        }
      }
    }

    System.out.println(best);
  }

  private static int compute(final int treeCount, final int treesBeforeNextGardener) {

    Status status = new Status();
    Action roundResult;
    do {
      roundResult = status.processRound(new Action().withShouldBuyGardener(true));
    } while (!roundResult.shouldBuyGardener());

    int treeBoughtSinceLastGardener = 0;

    while(status.treesSize() < treeCount) {
      if (treeBoughtSinceLastGardener >= treesBeforeNextGardener && status.gardenersSize() < treeCount/TREES_PER_GARDENER) {
        roundResult = status.processRound(
            new Action().withShouldBuyGardener(true));

        if (roundResult.shouldBuyGardener()) {
          treeBoughtSinceLastGardener = 0;
        }
      } else {
        int treesToBuy = Math.max(status.gardenersSize(), treesBeforeNextGardener - treeBoughtSinceLastGardener);
        roundResult = status.processRound(new Action().withTreesToBuy(treesToBuy));
        treeBoughtSinceLastGardener += roundResult.getTreesToBuy();
      }
    }

    // Convert as many points as possible until victory
    while (status.getRound() < MAX_LENGTH) {
      status.processRound(new Action().withBulletsToConvert(10000000));
      if (status.getVp() >= VICTORY_CONDITION) {
        System.out.println("Finished for values " + treeCount + ", " + treesBeforeNextGardener + " on round\t\t" + status.getRound());
        return status.getRound();
      }
    }
    System.out.println("Failed for values " + treeCount + ", " + treesBeforeNextGardener);
    return Integer.MAX_VALUE;
  }

  private static void write(String dest) {
    try {
      File file = new File(dest);
      file.createNewFile();

      PrintStream stdOut = System.out;
      PrintStream out = new PrintStream(new FileOutputStream(file, false));
      System.setOut(out);

//      for(int[] arr : vals) {
//        for (int i : arr) {
//          System.out.print(i + ",");
//        }
//        System.out.print("\n");
//      }

      System.setOut(stdOut);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
