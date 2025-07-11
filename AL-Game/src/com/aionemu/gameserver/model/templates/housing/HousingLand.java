package com.aionemu.gameserver.model.templates.housing;

import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Land", propOrder = { "addresses", "buildings", "sale", "fee", "caps" })
public class HousingLand {

	@XmlElementWrapper(name = "addresses", required = true)
	@XmlElement(name ="address")
	protected List<HouseAddress> addresses;
	
	@XmlElementWrapper(name = "buildings", required = true)
	@XmlElement(name ="building")
	protected List<Building> buildings;
	
	@XmlElement(required = true)
	protected Sale sale;
	
	@XmlElement(required = true)
	protected long fee;
	
	@XmlElement(required = true)
	protected BuildingCapabilities caps;
	
	@XmlAttribute(name = "sign_nosale", required = true)
	protected int signNosale;
	
	@XmlAttribute(name = "sign_sale", required = true)
	protected int signSale;
	
	@XmlAttribute(name = "sign_waiting", required = true)
	protected int signWaiting;
	
	@XmlAttribute(name = "sign_home", required = true)
	protected int signHome;
	
	@XmlAttribute(name = "manager_npc", required = true)
	protected int managerNpc;
	
	@XmlAttribute(name = "teleport_npc", required = true)
	protected int teleportNpc;
	
	@XmlAttribute(required = true)
	protected int id;

	public List<HouseAddress> getAddresses() {
		return addresses;
	}

	public List<Building> getBuildings() {
		return buildings;
	}
	
	public Building getDefaultBuilding() {
		for (Building building : buildings) {
			if (building.isDefault())
				return building;
		}
		return buildings.get(0); // fail
	}

	public Sale getSaleOptions() {
		return sale;
	}

	public long getMaintenanceFee() {
		return fee;
	}

	public BuildingCapabilities getCapabilities() {
		return caps;
	}

	public int getNosaleSignNpcId() {
		return signNosale;
	}

	public int getSaleSignNpcId() {
		return signSale;
	}

	public void setSignSale(int value) {
		this.signSale = value;
	}

	public int getWaitingSignNpcId() {
		return signWaiting;
	}

	public int getHomeSignNpcId() {
		return signHome;
	}

	public int getManagerNpcId() {
		return managerNpc;
	}

	public int getTeleportNpcId() {
		return teleportNpc;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}