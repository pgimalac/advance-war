package fr.main.model.buildings;

import fr.main.model.Player;
import fr.main.model.terrains.Terrain;
import fr.main.model.units.AbstractUnit;
import fr.main.model.units.land.LandUnit;

public class Barrack extends OwnableBuilding implements FactoryBuilding,RepairBuilding<LandUnit> {

	public static final int defense     = 3;
	public static final int income      = 1000;
	public static final String name     = "Caserne";
	public static final int maximumLife = 200;

	public Barrack(Terrain t, Player p){
		super(t, defense, p, maximumLife, income, name);
	}

	public boolean canRepair(AbstractUnit u){
		return u!=null && (u instanceof LandUnit);
	}

	public void repair(LandUnit u){
		if (canRepair(u)){
			u.addLife(20);
			if (u.getFuel()!=null)
				u.getFuel().replenish();
		}
	}
}
