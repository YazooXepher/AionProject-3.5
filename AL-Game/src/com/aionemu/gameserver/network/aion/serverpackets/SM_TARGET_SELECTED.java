package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Sweetkr
 */
public class SM_TARGET_SELECTED extends AionServerPacket {

	private int level;
	private int maxHp;
	private int currentHp;
	private int targetObjId;

	public SM_TARGET_SELECTED(Player player) {
		if (player != null) {
			if (player.getTarget() instanceof Creature) {
				this.level = ((Creature) player.getTarget()).getLevel();
				this.maxHp = ((Creature) player.getTarget()).getLifeStats().getMaxHp();
				this.currentHp = ((Creature) player.getTarget()).getLifeStats().getCurrentHp();
			}
			else {
				// TODO: check various gather on retail
				this.level = 0;
				this.maxHp = 0;
				this.currentHp = 0;
			}

			if (player.getTarget() != null)
				targetObjId = player.getTarget().getObjectId();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjId);
		writeH(level);
		writeD(maxHp);
		writeD(currentHp);
	}
}