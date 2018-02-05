package fr.main.model.terrains.naval;

import fr.main.model.units.Unit;

public class Sea implements NavalTerrain {

  public static final int defense = 1;

  public int getDefense (Unit u) {
    return defense;
  }

  public int getBonusVision (Unit u) {
    return 0;
  }

  public int getBonusRange (Unit u) {
    return 0;
  }

  public boolean isHiding (Unit u) {
    return false;
  }

  public String toString () {
    return "Mer";
  }

}
