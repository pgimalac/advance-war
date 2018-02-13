package fr.main.model.terrains.land;

import java.util.Map;
import java.util.HashMap;

import fr.main.model.units.Unit;
import fr.main.model.units.MoveType;
import fr.main.model.units.land.LandUnit;
import fr.main.model.terrains.Terrain;

public class Wood extends Terrain implements LandTerrain {

  private static Wood instance;

	protected static final Map<MoveType,Integer> sunnyWeatherMovementCosts=new HashMap<MoveType,Integer>();

	static{
		sunnyWeatherMovementCosts.put(MoveType.AIRY,1);
		sunnyWeatherMovementCosts.put(MoveType.TREAD,2);
		sunnyWeatherMovementCosts.put(MoveType.WHEEL,3);
		sunnyWeatherMovementCosts.put(MoveType.INFANTRY,1);
		sunnyWeatherMovementCosts.put(MoveType.MECH,1);
	}

	protected Wood() {
		super("Forêt",2, 0, 0, sunnyWeatherMovementCosts);
	}

	@Override
	public boolean isHiding(Unit u){
		return LandUnit.class.isInstance(u);
	}
	
  public static Wood get () {
    if (instance == null) instance = new Wood();
    return instance;
  }

}
