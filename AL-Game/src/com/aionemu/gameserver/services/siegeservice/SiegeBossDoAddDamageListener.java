package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author SoulKeeper
 */
@SuppressWarnings("rawtypes")
public class SiegeBossDoAddDamageListener extends AggroList.AddDamageValueCallback {

	private final Siege<?> siege;

	public SiegeBossDoAddDamageListener(Siege siege) {
		this.siege = siege;
	}

	@Override
	public void onDamageAdded(Creature creature, int damage) {
		siege.addBossDamage(creature, damage);
	}
}