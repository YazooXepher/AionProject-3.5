package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemUseAction")
public class ItemUseAction extends Action {

	@XmlAttribute(required = true)
	protected int itemid;

	@XmlAttribute(required = true)
	protected int count;

	@Override
	public void act(Skill skill) {
		if (skill.getEffector() instanceof Player) {
			Player player = (Player) skill.getEffector();
			Storage inventory = player.getInventory();

			if (!inventory.decreaseByItemId(itemid, count))
				return;
		}
	}
}