package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.controllers.HouseController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
public class CM_HOUSE_SETTINGS extends AionClientPacket {

	int permissions;
	int displaySign;
	String signAnnouncement;
	
	public CM_HOUSE_SETTINGS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		permissions = readC();
		displaySign = readC();
		signAnnouncement = readS();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null)
			return;
		
		House house = HousingService.getInstance().getPlayerStudio(player.getObjectId());
		if (house == null) {
			int address = HousingService.getInstance().getPlayerAddress(player.getObjectId());
			house = HousingService.getInstance().getHouseByAddress(address);
		}
		int settings = permissions << 8 | displaySign;
		house.setSettingFlags(settings);
		HouseController controller = (HouseController)house.getController();
		controller.updateAppearance();
		
		// TODO: save signAnnouncement; needs SM_HOUSE_RENDER packet fixed for that
		
		if (permissions == 1)
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_OPEN_DOOR);
		else if (permissions == 2) {
			house.getController().kickVisitors(player, false, true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_CLOSE_DOOR_WITHOUT_FRIENDS);
		}
		else if (permissions == 3) {
			house.getController().kickVisitors(player, true, true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_CLOSE_DOOR_ALL);
		}
	}
}