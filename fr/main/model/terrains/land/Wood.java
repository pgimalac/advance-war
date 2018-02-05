package fr.main.model.terrains.land;

import fr.main.model.units.Unit;
import fr.main.model.terrains.Terrain;

public class Wood extends Terrain implements LandTerrain {

	public Wood() {
		super(2, 0, 0, true);
	}

	public String toString () {
		return "Forêt";
	}
}
