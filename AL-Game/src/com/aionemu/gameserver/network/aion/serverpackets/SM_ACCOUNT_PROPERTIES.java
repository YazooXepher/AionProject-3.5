package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 *
 * @author pixfid
 */
public class SM_ACCOUNT_PROPERTIES extends AionServerPacket {

	public SM_ACCOUNT_PROPERTIES() {
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeH(0x03);
		writeH(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x8000);
		writeD(0x00);
		writeC(0x00);
		writeD(0x08);
		writeD(0x04);
	}
}