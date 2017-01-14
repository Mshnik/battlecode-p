package analysis;

/**
 * @author Mshnik
 */
class Action {

  final ActionEnum type;
  private Action(ActionEnum type) {
    this.type = type;
  }

  public static Action Nothing() {
    return new Action(ActionEnum.NOTHING);
  }

  public static Action BuyGardener() {
    return new Action(ActionEnum.BUY_GARDENER);
  }

  public static Action BuyTree() {
    return new Action(ActionEnum.BUY_TREE);
  }


  enum ActionEnum {
    NOTHING,
    BUY_GARDENER,
    BUY_TREE,
  }
}
