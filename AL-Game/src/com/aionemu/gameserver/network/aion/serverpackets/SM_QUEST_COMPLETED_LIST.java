package com.aionemu.gameserver.network.aion.serverpackets;

import javolution.util.FastList;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.questEngine.model.QuestState;

/**
 * @author MrPoke
 */
public class SM_QUEST_COMPLETED_LIST extends AionServerPacket {

	private FastList<QuestState> questState;

	public SM_QUEST_COMPLETED_LIST(FastList<QuestState> questState) {
		this.questState = questState;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeH(0x01); // 2.1
		writeH(-questState.size() & 0xFFFF);
		//QuestsData QUEST_DATA = DataManager.QUEST_DATA;
		for (QuestState qs : questState) {
			writeD(qs.getQuestId());
		//	writeH(QUEST_DATA.getQuestById(qs.getQuestId()).getCategory().getId());
			writeC(qs.getCompleteCount());
		}
		FastList.recycle(questState);
		questState = null;
	}
}