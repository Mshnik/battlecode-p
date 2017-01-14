package analysis;

/**
 * @author Mshnik
 */
class Action {
  private int bulletsToConvert;
  private boolean buyGardener;
  private int treesToBuy;

  Action() {}

  public int getBulletsToConvert() {
    return bulletsToConvert;
  }

  public boolean shouldBuyGardener() {
    return buyGardener;
  }

  public int getTreesToBuy() {
    return treesToBuy;
  }

  public Action withBulletsToConvert(int bulletsToConvert) {
    this.bulletsToConvert = bulletsToConvert;
    return this;
  }

  public Action withShouldBuyGardener(boolean shouldBuyGardener) {
    this.buyGardener = shouldBuyGardener;
    return this;
  }

  public Action withTreesToBuy(int treesToBuy) {
    this.treesToBuy = treesToBuy;
    return this;
  }
}
