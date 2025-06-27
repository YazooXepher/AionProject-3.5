package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob is sent for weapons. It keeps info about slots that weapon can be equipped to.
 * 
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class WeaponInfoBlobEntry extends ItemBlobEntry{

	WeaponInfoBlobEntry() {
		super(ItemBlobType.SLOTS_WEAPON);
	}

	@Override
	public
	void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;

		writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
		// TODO: check this, seems wrong
		writeQ(buf, item.hasFusionedItem() ? 0x00 : 0x02);
	}

	@Override
	public int getSize() {
		return 16;
	}
}