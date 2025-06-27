package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import java.util.Set;

/**
 * @author MrPoke, Rolandas
 */

public class SM_NEARBY_QUESTS extends AionServerPacket {

	private Set<Integer> questIds;

	public SM_NEARBY_QUESTS(Set<Integer> questIds) {
		this.questIds = questIds;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (questIds == null || con.getActivePlayer() == null)
			return;

		writeC(0);
		writeH(-questIds.size() & 0xFFFF);
		for (Integer questId : questIds) {
			writeD(questId);
        }
	}
}