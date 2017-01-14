package analysis;


import java.io.*;

/**
 * @author Mshnik
 */
public class Main {

  private static final int TREES_PER_GARDENER = (int)(Tree.WATER_HEAL_AMOUNT/(Tree.MAX_HEALTH*Tree.WITHER_RATIO));
  private static final int MAX_LENGTH = 3000;


  private static int[][] vals;

  public static void main(String[] args) {
    int maxTrees = 100;

    vals = new int[MAX_LENGTH][maxTrees];
    for (int i = 0; i < maxTrees; i++) {
      //System.out.println(String.format(i + "," + getVictoryLength(i)));
      Status s = compute(i);

      for (int x = 0; x < s.getRound(); x++) {
        vals[x][i] = s.getVpByRound().get(x);
      }
    }

    write("out/output.csv");
  }

  private static Status compute(int treeCount) {

    Status status = new Status();

    while(status.treesSize() < treeCount) {

      Status.Result r;
      do {
        r = status.processRound(Action.BuyGardener(), 0);
      } while (r != Status.Result.BOUGHT_GARDENER);

      int startCount = status.treesSize();
      while (status.treesSize() < treeCount && status.treesSize() - startCount < TREES_PER_GARDENER) {
        status.processRound(Action.BuyTree(), 0);
      }
    }

    // Convert as many points as possible until victory
    while (status.getRound() < MAX_LENGTH) {
      status.processRound(Action.Nothing(), 10000000);
    }
    return status;
  }

  private static void write(String dest) {
    try {
      File file = new File(dest);
      file.createNewFile();

      PrintStream stdOut = System.out;
      PrintStream out = new PrintStream(new FileOutputStream(file, false));
      System.setOut(out);

      for(int[] arr : vals) {
        for (int i : arr) {
          System.out.print(i + ",");
        }
        System.out.print("\n");
      }

      System.setOut(stdOut);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
