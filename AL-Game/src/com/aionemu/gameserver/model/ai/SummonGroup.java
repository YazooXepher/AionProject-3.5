package com.aionemu.gameserver.model.ai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonGroup")
public class SummonGroup {

	@XmlAttribute(name = "npcId")
	protected int npcId;
	@XmlAttribute(name = "x")
	protected float x;
	@XmlAttribute(name = "y")
	protected float y;
	@XmlAttribute(name = "z")
	protected float z;
	@XmlAttribute(name = "h")
	protected byte h;
	@XmlAttribute(name = "count")
	protected int count;
	@XmlAttribute(name = "minCount")
	protected int minCount;
	@XmlAttribute(name = "maxCount")
	protected int maxCount;
	@XmlAttribute(name = "distance")
	protected float distance;
	@XmlAttribute(name = "schedule")
	protected int schedule;

	public int getNpcId() {
		return npcId;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public byte getH() {
		return h;
	}

	public int getCount() {
		return count;
	}

	public int getMinCount() {
		return minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}
        
	public float getDistance() {
		return distance;
	}

	public int getSchedule() {
		return schedule;
	}
}