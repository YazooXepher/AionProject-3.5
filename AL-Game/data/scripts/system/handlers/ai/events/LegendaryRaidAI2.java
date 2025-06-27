package ai.events;

import org.joda.time.DateTime;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author Ever
 */
@AIName("legendaryraid")
public class LegendaryRaidAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
	}

	@Override
	protected void handleSpawned() {
		DateTime now = DateTime.now();
		int currentDay = now.getDayOfWeek();
		int currentHour = now.getHourOfDay();
		switch (getNpcId()) {
			case 281810: { // Omega
				if (currentDay == 1 && currentHour == 21) {
					super.handleSpawned();
				} else if (!isAlreadyDead()) {
					getOwner().getController().onDelete();
				}
				break;
			}
			case 281811: { // Ragnarok
				if (currentDay == 1 && currentHour == 19) {
					super.handleSpawned();
				} else if (!isAlreadyDead()) {
					getOwner().getController().onDelete();
				}
				break;
			}
		}
	}

	@Override
	protected void handleDespawned() {
		DateTime now = DateTime.now();
		int currentDay = now.getDayOfWeek();
		int currentHour = now.getHourOfDay();
		switch (getNpcId()) {
			case 281810: { // Omega
				if (currentDay == 1 && currentHour >= 22) {
					super.handleDespawned();
				} else if (!isAlreadyDead()) {
					getOwner().getController().onDelete();
				}
				break;
			}
			case 281811: { // Ragnarok
				if (currentDay == 1 && currentHour == 20) {
					super.handleDespawned();
				} else if (!isAlreadyDead()) {
					getOwner().getController().onDelete();
				}
				break;
			}
		}
	}
}