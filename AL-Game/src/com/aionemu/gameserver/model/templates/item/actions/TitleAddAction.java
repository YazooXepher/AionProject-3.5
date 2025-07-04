package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Hilgert
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TitleAddAction")
public class TitleAddAction extends AbstractItemAction {

	@XmlAttribute
	protected int titleid;
  @XmlAttribute
  protected Integer minutes;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.gameserver.itemengine.actions.AbstractItemAction#canAct(com.aionemu.gameserver.model.gameobjects.player
	 * .Player, com.aionemu.gameserver.model.gameobjects.Item, com.aionemu.gameserver.model.gameobjects.Item)
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (titleid == 0 || parentItem == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		if (player.getTitleList().contains(titleid)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_TOOLTIP_LEARNED_TITLE);
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.gameserver.itemengine.actions.AbstractItemAction#act(com.aionemu.gameserver.model.gameobjects.player
	 * .Player, com.aionemu.gameserver.model.gameobjects.Item, com.aionemu.gameserver.model.gameobjects.Item)
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		ItemTemplate itemTemplate = parentItem.getItemTemplate();
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(itemTemplate.getNameId())));
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);

		if (player.getTitleList().addTitle(titleid, false, minutes == null ? 0 : ((int)(System.currentTimeMillis()/1000))+minutes*60)) {
			Item item = player.getInventory().getItemByObjId(parentItem.getObjectId());
			player.getInventory().delete(item);
		}
	}
}