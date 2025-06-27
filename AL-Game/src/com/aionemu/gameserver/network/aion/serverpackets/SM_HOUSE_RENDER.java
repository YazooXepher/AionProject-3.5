package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.HouseDecoration;
import com.aionemu.gameserver.model.gameobjects.SummonedHouseNpc;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.BuildingType;
import com.aionemu.gameserver.model.templates.housing.PartType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.mysql.jdbc.StringUtils;

/**
 * @author Rolandas
 */
public class SM_HOUSE_RENDER extends AionServerPacket {

	private House house;

	public SM_HOUSE_RENDER(House house) {
		this.house = house;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(0);
		writeD(house.getAddress().getId());

		int playerObjectId = house.getOwnerId();
		writeD(playerObjectId);
		writeD(BuildingType.PERSONAL_FIELD.getId());

		if (house.getSettingFlags() == 0) {
			writeC(playerObjectId == 0 ? 0 : 1); // show owner name
		}
		else {
			writeC(house.getSettingFlags() & 0xF); // hide owner name
		}

		writeD(house.getBuilding().getId());
		writeC(house.getHousingFlags()); // unk 2 or 3 without owner, 5 or 3 with owner

		int doorState = house.getSettingFlags() >> 8;
		writeC(doorState); // 1 - opened doors; 2 - open friends; 3 - closed doors

		int dataSize = 187;
		if (house.getButler() != null) {
			SummonedHouseNpc butler = (SummonedHouseNpc) house.getButler();
			if (!StringUtils.isNullOrEmpty(butler.getMasterName())) {
				dataSize -= (butler.getMasterName().length() + 1) * 2;
				writeS(butler.getMasterName()); // owner name
			}
		}

		// TODO: various messages, some crypted tags
		for (int i = 0; i < dataSize; i++)
			writeC(0);

		writePartData(house, PartType.ROOF, true);
		writePartData(house, PartType.OUTWALL, true);
		writePartData(house, PartType.FRAME, true);
		writePartData(house, PartType.DOOR, true);
		writePartData(house, PartType.GARDEN, true);
		writePartData(house, PartType.FENCE, true);

		writePartData(house, PartType.INWALL_ANY, false);

		HouseDecoration defaultDecor = null;
		if (house.getBuilding().getType() == BuildingType.PERSONAL_FIELD)
			defaultDecor = house.getDefaultPart(PartType.INWALL_ANY);

		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());
		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());
		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());
		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());
		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());

		writePartData(house, PartType.INFLOOR_ANY, false);

		defaultDecor = null;
		if (house.getBuilding().getType() == BuildingType.PERSONAL_FIELD)
			defaultDecor = house.getDefaultPart(PartType.INFLOOR_ANY);

		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());
		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());
		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());
		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());
		writeD(defaultDecor == null ? 0 : defaultDecor.getTemplate().getId());

		writePartData(house, PartType.ADDON, true);
		writeD(0);
		writeD(0);
		writeC(0);

		// Emblem and color?
		writeC(0);
		writeC(0);
		writeD(0); // color

	}

	private void writePartData(House house, PartType partType, boolean skipPersonal) {
		boolean isPersonal = house.getBuilding().getType() == BuildingType.PERSONAL_INS;
		HouseDecoration deco = house.getRenderPart(partType);
		if (skipPersonal && isPersonal)
			writeD(0);
		else
			writeD(deco != null ? deco.getTemplate().getId() : 0);
	}
}