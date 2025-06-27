package com.aionemu.gameserver.model.drop;

import java.nio.ByteBuffer;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "drop")
public class Drop implements DropCalculator {

    @XmlAttribute(name = "item_id", required = true)
    protected int itemId;
    @XmlAttribute(name = "min_amount", required = true)
    protected int minAmount;
    @XmlAttribute(name = "max_amount", required = true)
    protected int maxAmount;
    @XmlAttribute(required = true)
    protected float chance;
    @XmlAttribute(name = "no_reduce")
    protected Boolean noReduce = false;
    @XmlAttribute(name = "eachmember")
    protected boolean eachMember = false;
    private ItemTemplate template;

    public Drop() {
    }

	public Drop(int itemId, int minAmount, int maxAmount, float chance, boolean noReduce) {
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.chance = chance;
		this.noReduce = noReduce;
		template = DataManager.ITEM_DATA.getItemTemplate(itemId);
	}

	public ItemTemplate getItemTemplate() {
		return template == null ? DataManager.ITEM_DATA.getItemTemplate(itemId) : template;
	}

	/**
	 * Gets the value of the itemId property.
	 *
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * Gets the value of the minAmount property.
	 *
	 */
	public int getMinAmount() {
		return minAmount;
	}

	/**
	 * Gets the value of the maxAmount property.
	 *
	 */
	public int getMaxAmount() {
		return maxAmount;
	}

	/**
	 * Gets the value of the chance property.
	 *
	 */
	public float getChance() {
		return chance;
	}

	public boolean isNoReduction() {
		return noReduce;
	}

	public Boolean isEachMember() {
		return eachMember;
	}

	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers) {
		float percent = chance;
		if (!noReduce) {
			percent *= dropModifier;
		}
		if (Rnd.get() * 100 < percent) {
			if (eachMember && groupMembers != null && !groupMembers.isEmpty()) {
				for (Player player : groupMembers) {
					DropItem dropitem = new DropItem(this);
					dropitem.calculateCount();
					dropitem.setIndex(index++);
					dropitem.setPlayerObjId(player.getObjectId());
					dropitem.setWinningPlayer(player);
					dropitem.isDistributeItem(true);
					result.add(dropitem);
				}
			}
			else {
				DropItem dropitem = new DropItem(this);
				dropitem.calculateCount();
				dropitem.setIndex(index++);
				result.add(dropitem);
			}
		}
		return index;
	}

	public static Drop load(ByteBuffer buffer){
		Drop drop = new Drop();
		drop.itemId = buffer.getInt();
		drop.chance = buffer.getFloat();
		drop.minAmount = buffer.getInt();
		drop.maxAmount = buffer.getInt();
		drop.noReduce = buffer.get() == 1? true : false;
		drop.eachMember = buffer.get() == 1? true : false;
		return drop;
	}

	@Override
	public String toString() {
		return "Drop [itemId=" + itemId + ", minAmount=" + minAmount + ", maxAmount=" + maxAmount + ", chance=" + chance
			+ ", noReduce=" + noReduce + ", eachMember=" + eachMember + "]";
	}
}