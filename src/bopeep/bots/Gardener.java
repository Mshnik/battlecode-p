package bopeep.bots;

import static bopeep.bots.CommonFunc.*;

import battlecode.common.*;
import bopeep.RobotPlayer;
import java.util.Iterator;
import util.RingBuffer;

/**
 * @author Mshnik
 */
// TODO: unroll constant for loops if bytecode costs get too high.
public strictfp class Gardener {

  private static final int GARDENER_STRIDE_RADIUS = 1;
  private static final int TREE_MATURITY_ROUNDS = 80;

  private static final int TREE_MAX = 10;
  private static final float PLANT_DISTANCE_FROM_ORIGIN = 2.5f;
  private static final float MAX_DISTANCE_FROM_ORIGIN = 3.5f;
  // Calculated here
  // http://www.calculatorsoup.com/calculators/geometry-plane/triangle-theorems.php
  private static final float TREE_PLACEMENT_RADIANS = 0.628319f;

  /** Location when this gardnener is spawned. Doesn't change after being set */
  private static MapLocation origin;
  /** Locations where the gardener has to move to to plant the 10 trees. Doesn't change after being set */
  private static MapLocation[] plantingLocations;
  /** Directions to plant trees when standing at each planting locaiton. Doesn't change after being set */
  private static Direction[] plantingDirections;
  /** Locations where the 10 trees are to be planted. Doesn't change after being set */
  private static MapLocation[] treeLocations;
  /** True once the tree at the given index is mature. False until there is a planted tree that becomes mature */
  private static int[] treeLifeRounds;

  /** current location of this Gardener. Updated as necessary */
  private static MapLocation currentLocation;
  /** Amount of move distance left. Updated as necessary */
  private static float moveDistLeft;
  /** Number of turns since planting a tree. Checked against the cooldown */
  private static int turnsSincePlanting;

  public static void run() {
    System.out.println("I'm a gardener!");

    origin = RobotPlayer.rc.getLocation();
    treeLocations = new MapLocation[TREE_MAX];
    plantingDirections = new Direction[TREE_MAX];
    treeLifeRounds = new int[TREE_MAX];
    plantingLocations = new MapLocation[TREE_MAX];
    for(int i = 0; i < TREE_MAX; i++) {
      float radians = TREE_PLACEMENT_RADIANS * i;
      plantingDirections[i] = new Direction(radians);
      plantingLocations[i] = origin.add(radians, PLANT_DISTANCE_FROM_ORIGIN);
      treeLocations[i] = origin.add(radians, MAX_DISTANCE_FROM_ORIGIN);
    }

    while (true) {
      // Update this gardener's info
      currentLocation = RobotPlayer.rc.getLocation();
      moveDistLeft = GARDENER_STRIDE_RADIUS;

      //Check all trees to see if they're alive
      TreeInfo[] treeInfos = checkTrees();

      // Try to plant a tree, if possible.
      if (treeInfos.length < TREE_MAX
          && turnsSincePlanting >= GameConstants.BULLET_TREE_CONSTRUCTION_COOLDOWN
          && RobotPlayer.rc.getTeamBullets() >= GameConstants.BULLET_TREE_COST) {
        if (tryPlantTree(treeInfos)) {
          turnsSincePlanting = 0;
        } else {
          turnsSincePlanting ++;
        }
      }

      // Try to water a tree
      tryWaterTree(treeInfos);

      // Move as close to origin as possible
      try {
        Direction dirToOrigin = currentLocation.directionTo(origin);
        if (dirToOrigin != null && moveDistLeft > 0) {
          RobotPlayer.rc.move(dirToOrigin, moveDistLeft);
        }
      } catch (GameActionException e) {
        e.printStackTrace();
      }

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
    for(int i = 0; i < TREE_MAX; i++) {
      try {
        treeInfos[i] = RobotPlayer.rc.senseTreeAtLocation(treeLocations[i]);
        if (treeInfos[i] != null) {
          treeLifeRounds[i]++;
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
    for(int i = 0; i < TREE_MAX; i++) {
      if (treeInfos[i] == null && plantingLocations[i].isWithinDistance(currentLocation, GARDENER_STRIDE_RADIUS)) {
        try {
          RobotPlayer.rc.move(plantingLocations[i]);
          currentLocation = RobotPlayer.rc.getLocation();
          moveDistLeft -= currentLocation.distanceTo(plantingLocations[i]);
          RobotPlayer.rc.plantTree(plantingDirections[i]);
          return true;
        } catch (GameActionException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  private static boolean tryWaterTree(TreeInfo[] treeInfos) {
    for(int i = 0; i < TREE_MAX; i++) {
      if (treeInfos[i] != null
          && treeLifeRounds[i] > TREE_MATURITY_ROUNDS
          && treeLocations[i].isWithinDistance(currentLocation, moveDistLeft + GARDENER_STRIDE_RADIUS)) {
        Direction directionToDest = currentLocation.directionTo(treeLocations[i]);
        float dist = currentLocation.distanceTo(treeLocations[i]) - GARDENER_STRIDE_RADIUS;
        try {
          RobotPlayer.rc.move(directionToDest, dist);
          currentLocation = RobotPlayer.rc.getLocation();
          moveDistLeft -= dist;
          RobotPlayer.rc.water(treeLocations[i]);
          return true;
        } catch (GameActionException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }
}
