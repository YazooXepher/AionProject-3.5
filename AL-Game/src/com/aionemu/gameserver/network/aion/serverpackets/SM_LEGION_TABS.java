package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.team.legion.LegionHistory;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import java.util.Collection;

/**
	 * @author Simple, KID, xTz
 */
public class SM_LEGION_TABS extends AionServerPacket {

	private int page;
	private Collection<LegionHistory> legionHistory;
	private int tabId;

	public SM_LEGION_TABS(Collection<LegionHistory> legionHistory, int tabId) {
		this.legionHistory = legionHistory;
		this.page = 0;
		this.tabId = tabId;
	}

	public SM_LEGION_TABS(Collection<LegionHistory> legionHistory, int page, int tabId) {
		this.legionHistory = legionHistory;
		this.page = page;
		this.tabId = tabId;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		int size = legionHistory.size();
		/**
		 * If history size is less than page*8 return
		 */
		if (size < (page * 8))
			return;

		// TODO: Formula's could use a refactor
		int hisSize = size - (page * 8);
		if(size > (page + 1) * 8)
			hisSize = 8;

		writeD(size);
		writeD(page); // current page
		writeD(hisSize);

		int i = 0;
		for (LegionHistory history : legionHistory) {
			if (i >= (page * 8) && i <= (8 + (page * 8))) {
				writeD((int) (history.getTime().getTime() / 1000));
				writeC(history.getLegionHistoryType().getHistoryId());
				writeC(0); // unk
				writeS(history.getName(), 64);
				writeH(0); // separator
				writeS(history.getDescription(), 64);
				writeD(0);
			}
			i++;
			if (i >= (8 + (page * 8)))
				break;
		}
		writeC(tabId);
		writeC(0);
	}
}