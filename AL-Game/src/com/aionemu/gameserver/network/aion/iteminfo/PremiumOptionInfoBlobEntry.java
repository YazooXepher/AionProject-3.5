package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * @author Rolandas
 */
public class PremiumOptionInfoBlobEntry extends ItemBlobEntry {

	public PremiumOptionInfoBlobEntry() {
		super(ItemBlobType.PREMIUM_OPTION);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		// TODO: 1 if all values of random bonuses are multiplied by 2;
		// however, have to be handled onItemEquipment
		// writeC(buf, owner.getPlayerAccount().getMembership() > 0 ? 1 : 0);
		writeC(buf, 0);
	}

	@Override
	public int getSize() {
		return 1;
	}
}