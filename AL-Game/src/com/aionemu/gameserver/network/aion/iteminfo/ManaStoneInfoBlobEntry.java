package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob sends info about mana stones.
 * 
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class ManaStoneInfoBlobEntry extends ItemBlobEntry {

	ManaStoneInfoBlobEntry() {
		super(ItemBlobType.MANA_SOCKETS);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;

		writeC(buf, item.isSoulBound() ? 1 : 0);
		writeC(buf, item.getEnchantLevel()); // enchant (1-15)
		writeD(buf, item.getItemSkinTemplate().getTemplateId());
		writeC(buf, item.getOptionalSocket());
		writeC(buf, item.getItemTemplate().getMaxEnchantLevel());

		writeItemStones(buf);

		ItemStone god = item.getGodStone();
		writeD(buf, god == null ? 0 : god.getItemId());
		
		int itemColor = item.getItemColor();
		int dyeExpiration = item.getColorTimeLeft();
		// expired dyed items
		if ((dyeExpiration > 0 && item.getColorExpireTime() > 0 || dyeExpiration == 0 && item.getColorExpireTime() == 0)
			&& item.getItemTemplate().isItemDyePermitted()) {
			writeC(buf, itemColor == 0 ? 0 : 1);
			writeD(buf, itemColor);
			writeD(buf, 0); // unk 1.5.1.9
			writeD(buf, dyeExpiration); // seconds until dye expires
		}
		else {
			writeC(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0); // unk 1.5.1.9
			writeD(buf, 0);
		}
	}

	/**
	 * Writes manastones
	 * 
	 * @param item
	 */
	private void writeItemStones(ByteBuffer buf) {
		Item item = ownerItem;
		int count = 0;

		if (item.hasManaStones()) {
			Set<ManaStone> itemStones = item.getItemStones();

			for (ManaStone itemStone : itemStones) {
				if (count == 6)
					break;
				writeD(buf, itemStone.getItemId());
				count++;
			}
			skip(buf, (6 - count) * 4);
		}
		else {
			skip(buf, 24);
		}
	}

	@Override
	public int getSize() {
		return 12 * 2 + 24 + 1;
	}
}