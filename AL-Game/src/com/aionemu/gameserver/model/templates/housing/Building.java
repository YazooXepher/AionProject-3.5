package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.*;

import com.aionemu.gameserver.dataholders.DataManager;
import com.mysql.jdbc.StringUtils;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "parts" })
@XmlRootElement(name = "building")
public class Building {

  private Parts parts;

	@XmlAttribute(name = "default")
	protected boolean isDefault;

	@XmlAttribute(name = "parts_match")
	protected String partsMatch;

	@XmlAttribute
	protected String size;

	@XmlAttribute
	protected BuildingType type;

	@XmlAttribute(required = true)
	protected int id;

	public boolean isDefault() {
		return isDefault;
	}

	// All methods for DataManager call are just to ensure integrity 
	// if called from housing land templates, because it only has id and isDefault 
	// for the buildings. Buildings template has full info though, except isDefault
	// value for the land.

	public String getPartsMatchTag() {
		if (StringUtils.isNullOrEmpty(partsMatch))
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getPartsMatchTag();
		return partsMatch;
	}

	public String getSize() {
		if (StringUtils.isNullOrEmpty(size))
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getSize();
		return size;
	}

	public BuildingType getType() {
		if (type == null)
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getType();
		return type;
	}

	public int getId() {
		return id;
	}

	public Parts getDefaultParts() {
		if (parts == null)
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getDefaultParts();
		return parts;
	}
}