package com.aionemu.gameserver.model.templates.vortex;

import com.aionemu.gameserver.world.WorldPosition;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HomePoint")
public class HomePoint {

	@XmlAttribute(name = "map")
	protected int map;
	@XmlAttribute(name = "x")
	protected float x;
	@XmlAttribute(name = "y")
	protected float y;
	@XmlAttribute(name = "z")
	protected float z;
	@XmlAttribute(name = "h")
	protected byte h;

	public int getWorldId() {
		return map;
	}

	public WorldPosition getHomePoint() {
		WorldPosition home = new WorldPosition();
		home.setMapId(map);
		home.setXYZH(x, y, z, h);
		return home;
	}
}