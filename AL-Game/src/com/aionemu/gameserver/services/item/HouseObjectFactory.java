package com.aionemu.gameserver.services.item;

import org.apache.commons.lang.IncompleteArgumentException;
import org.joda.time.DateTime;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.ChairObject;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.JukeBoxObject;
import com.aionemu.gameserver.model.gameobjects.MoveableObject;
import com.aionemu.gameserver.model.gameobjects.NpcObject;
import com.aionemu.gameserver.model.gameobjects.PassiveObject;
import com.aionemu.gameserver.model.gameobjects.PictureObject;
import com.aionemu.gameserver.model.gameobjects.PostboxObject;
import com.aionemu.gameserver.model.gameobjects.StorageObject;
import com.aionemu.gameserver.model.gameobjects.UseableItemObject;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.*;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.SummonHouseObjectAction;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

/**
 * @author Rolandas
 */
public final class HouseObjectFactory {

	/**
	 * For loading data from DB
	 */
	public static HouseObject<?> createNew(House house, int objectId, int objectTemplateId) {
		PlaceableHouseObject template = DataManager.HOUSING_OBJECT_DATA.getTemplateById(objectTemplateId);
		if (template instanceof HousingChair)
			return new ChairObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingJukeBox)
			return new JukeBoxObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingMoveableItem)
			return new MoveableObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingNpc)
			return new NpcObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingPicture)
			return new PictureObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingPostbox)
			return new PostboxObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingStorage)
			return new StorageObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingUseableItem)
			return new UseableItemObject(house, objectId, template.getTemplateId());
		return new PassiveObject(house, objectId, template.getTemplateId());
	}
	
	/**
	 * For transferring item from inventory to house registry
	 */
	public static HouseObject<?> createNew(House house, ItemTemplate itemTemplate) {
		if (itemTemplate.getActions() == null)
			throw new IncompleteArgumentException("template actions null");
		SummonHouseObjectAction action = itemTemplate.getActions().getHouseObjectAction();
		if (action == null)
			throw new IncompleteArgumentException("template actions miss SummonHouseObjectAction");
		
		int objectTemplateId = action.getTemplateId();
		HouseObject<?> obj = createNew(house, IDFactory.getInstance().nextId() , objectTemplateId);
		if (obj.getObjectTemplate().getUseDays() > 0) {
			int expireEnd = (int) (DateTime.now().plusDays(obj.getObjectTemplate().getUseDays()).getMillis() / 1000);
			obj.setExpireTime(expireEnd);
		}
		return obj;
	}
}