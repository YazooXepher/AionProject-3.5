package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlaceableHouseObject")
@XmlSeeAlso(
{ 
	HousingJukeBox.class, HousingPicture.class, HousingPostbox.class, 
	HousingChair.class, HousingStorage.class, HousingNpc.class, HousingMoveableItem.class, 
	HousingUseableItem.class, HousingPassiveItem.class 
})
public abstract class PlaceableHouseObject extends AbstractHouseObject {

	@XmlAttribute(name = "use_days")
	protected Integer useDays;

	@XmlAttribute
	protected LimitType limit;

	@XmlAttribute
	protected PlaceLocation location;

	@XmlAttribute
	protected PlaceArea area;

	/**
	 * Gets the value of the useDays property.
	 * 
	 * @return null if not restricted
	 */
	public int getUseDays() {
		if (useDays == null)
			return 0;
		return useDays;
	}

	/**
	 * Where the object is allowed to be placed on?
	 * <p>
	 * <tt>TODO: check if it is needed and not handled by the client</tt>
	 * 
	 * @return {@link LimitType.NONE} if no restriction
	 */
	public LimitType getPlacementLimit() {
		if (limit == null)
			return LimitType.NONE;
		return limit;
	}

	/**
	 * How the object is allowed to be placed (stacks, ground, wall) ?
	 * <p>
	 * <tt>TODO: check if it is needed and not handled by the client</tt>
	 * 
	 * @return possible object is {@link PlaceLocation }
	 */
	public PlaceLocation getLocation() {
		return location;
	}

	/**
	 * Environment where the object is allowed to be placed (interior, exterior)
	 * <p>
	 * <tt>TODO: check if it is needed and not handled by the client</tt>
	 * 
	 * @return possible object is {@link PlaceArea }
	 */
	public PlaceArea getArea() {
		return area;
	}
	public abstract byte getTypeId();
}