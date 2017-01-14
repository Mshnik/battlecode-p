package bopeep.bots;

import static bopeep.bots.CommonFunc.*;

import battlecode.common.*;
import bopeep.RobotPlayer;

import java.util.Arrays;
import java.util.Iterator;
import util.RingBuffer;

/**
 * @author Mshnik
 */
// TODO: unroll constant for loops if bytecode costs get too high.
public strictfp class Gardener {

  private static final int GARDENER_STRIDE_RADIUS = 1;
  private static final int TREE_MATURITY_ROUNDS = 80;

  private static final int TREE_MAX = 6;
  private static final float MAX_DISTANCE_FROM_ORIGIN = 2f;
  // Calculated here
  // http://www.calculatorsoup.com/calculators/geometry-plane/triangle-theorems.php
  private static final float TREE_PLACEMENT_RADIANS = 1.0472f;

  /** Directions to plant trees when standing at each planting locations. Doesn't change after being set */
  private static Direction[] plantingDirections;
  /** Locations where the 10 trees are to be planted. Doesn't change after being set */
  private static MapLocation[] treeLocations;
  /** Number of trees that exist. Refreshed every round */
  private static int treeCount;
  /** True once the tree at the given index is mature. False until there is a planted tree that becomes mature */
  private static int[] treeLifeRounds;

  /** Number of turns since planting a tree. Checked against the cooldown */
  private static int turnsSincePlanting;

  public static void run() {
    MapLocation origin = RobotPlayer.rc.getLocation();
    treeLocations = new MapLocation[TREE_MAX];
    plantingDirections = new Direction[TREE_MAX];
    treeLifeRounds = new int[TREE_MAX];
    for(int i = 0; i < TREE_MAX; i++) {
      float radians = TREE_PLACEMENT_RADIANS * i;
      plantingDirections[i] = new Direction(radians);
      treeLocations[i] = origin.add(radians, MAX_DISTANCE_FROM_ORIGIN);
    }
    turnsSincePlanting = 1000;

    while (true) {

      //Check all trees to see if they're alive
      TreeInfo[] treeInfos = checkTrees();
      System.out.println("Got Tree Infos: " + Arrays.toString(treeInfos));

      // Try to plant a tree, if possible.
      if (treeCount < TREE_MAX
          && turnsSincePlanting > GameConstants.BULLET_TREE_CONSTRUCTION_COOLDOWN
          && RobotPlayer.rc.getTeamBullets() >= GameConstants.BULLET_TREE_COST) {
        if (tryPlantTree(treeInfos)) {
          turnsSincePlanting = 0;
        }
      }
      turnsSincePlanting ++;

      // Try to water a tree
      tryWaterTree(treeInfos);

      // Wait until the next turn, then it will perform this loop again
      Clock.yield();
    }
  }

  /**
   * Checks all trees to make sure they're alive.
   *
   * Bytecode cost:
   *
   * Returns an TreeInfo array current for this round.
   * May have nulls if not all trees exist.
   */
  private static TreeInfo[] checkTrees() {
    TreeInfo[] treeInfos = new TreeInfo[TREE_MAX];
    treeCount = 0;
    for(int i = 0; i < TREE_MAX; i++) {
      try {
        treeInfos[i] = RobotPlayer.rc.senseTreeAtLocation(treeLocations[i]);
        if (treeInfos[i] != null) {
          treeLifeRounds[i]++;
          treeCount++;
        } else {
          treeLifeRounds[i] = 0;
        }
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    }
    return treeInfos;
  }

  private static boolean tryPlantTree(TreeInfo[] treeInfos) {
    System.out.println("Trying to plant");
    for(int i = 0; i < TREE_MAX; i++) {
      System.out.println(">>Trying direction " + plantingDirections[i]);
      if (treeInfos[i] == null) {
        try {
          RobotPlayer.rc.plantTree(plantingDirections[i]);
          System.out.println(">>>Planting succeeded");
          return true;
        } catch (GameActionException e) {
          System.out.println(">>>Planting failed");
        }
      }
    }
    return false;
  }

  private static boolean tryWaterTree(TreeInfo[] treeInfos) {
    System.out.println("Trying to water");
    // Find the mature tree with the least health, water that one
    int bestTreeIndex = -1;
    float treeLowestHealth = GameConstants.BULLET_TREE_MAX_HEALTH;
    for(int i = 0; i < TREE_MAX; i++) {
      if (treeInfos[i] != null
          && treeLifeRounds[i] > TREE_MATURITY_ROUNDS
          && treeInfos[i].health < treeLowestHealth) {
        bestTreeIndex = i;
        treeLowestHealth = treeInfos[i].health;
      }
    }

    if (bestTreeIndex != -1) {
      try {
        RobotPlayer.rc.water(treeLocations[bestTreeIndex]);
        System.out.println(">>>Watering succeeded");
        return true;
      } catch (GameActionException e) {
        System.out.println(">>>Watering failed");
      }
    }
    return false;
  }
}
