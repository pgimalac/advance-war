package fr.main.model.terrains.land;

import java.util.Map;
import java.util.HashMap;

import fr.main.model.units.Unit;
import fr.main.model.units.MoveType;
import fr.main.model.terrains.Buildable;
import fr.main.model.buildings.Building;
import fr.main.model.terrains.Terrain;

public class Hill extends Buildable implements LandTerrain {

	private static Hill instance;
	protected static final Map<MoveType,Integer> sunnyWeatherMovementCosts=new HashMap<MoveType,Integer>();

	static{
		sunnyWeatherMovementCosts.put(MoveType.AIRY,1);
		sunnyWeatherMovementCosts.put(MoveType.WHEEL,3);
		sunnyWeatherMovementCosts.put(MoveType.TREAD,2);
		sunnyWeatherMovementCosts.put(MoveType.INFANTRY,1);
		sunnyWeatherMovementCosts.put(MoveType.MECH,1);
	}

	protected Hill() {
		super("Colline", 2, 0, 0, sunnyWeatherMovementCosts);
	}

	public static Hill get () {
		if (instance == null)
			instance = new Hill();
		return instance;
	}
}
