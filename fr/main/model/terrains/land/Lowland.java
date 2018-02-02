package fr.main.model.terrains.land;

import fr.main.model.units.Unit;
import fr.main.model.terrains.Buildable;
import fr.main.model.buildings.Building;

public class Lowland implements LandTerrain,Buildable {

  public static final int defense = 1;

  private Building building;

  {
    building=null;
  }

  public void setBuilding(Building building){
    this.building=building;
  }

  public Building getBuilding(){
    return this.building;
  }

  public int getDefense (Unit u) {
    if (building==null)
      return this.defense;
    else
      return building.getDefense()+this.defense/2;
      // ainsi un batiment construit sur une plaine ne verra pas sa défense modifiée mais un batiment construit sur une colline aura +1 de défense
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
    return "Plaine";
  }
}
