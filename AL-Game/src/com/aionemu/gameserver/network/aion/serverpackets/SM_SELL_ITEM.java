package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author orz, Sarynth
 */
public class SM_SELL_ITEM extends AionServerPacket {

	private int targetObjectId;
	private int sellPercentage;

	public SM_SELL_ITEM(int targetObjectId, int sellPercentage) {

		this.sellPercentage = sellPercentage;
		this.targetObjectId = targetObjectId;

	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected void writeImpl(AionConnection con) {

		writeD(targetObjectId);
		writeD(sellPercentage); // Buy Price * (sellPercentage / 100) = Display price.

	}
}