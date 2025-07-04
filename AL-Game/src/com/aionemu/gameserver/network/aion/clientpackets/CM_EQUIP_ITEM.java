package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Avol modified by ATracer
 */
public class CM_EQUIP_ITEM extends AionClientPacket {

	public long slotRead;
	public int itemUniqueId;
	public int action;

	public CM_EQUIP_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		action = readC(); // 0/1 = equip/unequip
		slotRead = readQ();
		itemUniqueId = readD();
	}

	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();

		activePlayer.getController().cancelUseItem();

		Equipment equipment = activePlayer.getEquipment();
		Item resultItem = null;

		if (!RestrictionsManager.canChangeEquip(activePlayer))
			return;

		switch (action) {
			case 0:
				resultItem = equipment.equipItem(itemUniqueId, slotRead);
				break;
			case 1:
				resultItem = equipment.unEquipItem(itemUniqueId, slotRead);
				break;
			case 2:
				if (activePlayer.getController().hasTask(TaskId.ITEM_USE) && !activePlayer.getController().getTask(TaskId.ITEM_USE).isDone()) {
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_CANT_EQUIP_ITEM_IN_ACTION);
					return;
				}
				equipment.switchHands();
				break;
		}

		if (resultItem != null || action == 2) {
			PacketSendUtility.broadcastPacket(activePlayer, new SM_UPDATE_PLAYER_APPEARANCE(activePlayer.getObjectId(),
				equipment.getEquippedForApparence()), true);
		}
	}
}