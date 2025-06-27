package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob is sending info about the item that were fused with current item.
 * 
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class CompositeItemBlobEntry extends ItemBlobEntry {

	CompositeItemBlobEntry() {
		super(ItemBlobType.COMPOSITE_ITEM);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;

		writeD(buf, item.getFusionedItemId());
		writeFusionStones(buf);
		writeH(buf, item.hasOptionalFusionSocket() ? item.getOptionalFusionSocket() : 0x00);
	}

	private void writeFusionStones(ByteBuffer buf) {
		Item item = ownerItem;
		int count = 0;

		if (item.hasFusionStones()) {
			Set<ManaStone> itemStones = item.getFusionStones();

			for (ManaStone itemStone : itemStones) {
				if (count++ == 6)
					break;
				writeD(buf, itemStone.getItemId());
			}
			skip(buf, (6 - count) * 4);
		}
		else {
			skip(buf, 24);
		}
	}

	@Override
	public int getSize() {
		return 12 * 2 + 6;
	}
}