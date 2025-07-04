package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.actions.EnchantItemAction;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, Wakizashi
 *
 */
public class CM_MANASTONE extends AionClientPacket {

	private int npcObjId;
	private int slotNum;
	private int actionType;
	private int targetFusedSlot;
	private int stoneUniqueId;
	private int targetItemUniqueId;
	private int supplementUniqueId;
	@SuppressWarnings("unused")
	private ItemCategory actionCategory;

	/**
	 * @param opcode
	 */
	public CM_MANASTONE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionType = readC();
		targetFusedSlot = readC();
		targetItemUniqueId = readD();
		switch (actionType) {
			case 1:
			case 2:
				stoneUniqueId = readD();
				supplementUniqueId = readD();
				break;
			case 3:
				slotNum = readC();
				readC();
				readH();
				npcObjId = readD();
				break;
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		VisibleObject obj = player.getKnownList().getObject(npcObjId);

		switch (actionType) {
			case 1: // enchant stone
			case 2: // add manastone
				EnchantItemAction action = new EnchantItemAction();
				Item manastone = player.getInventory().getItemByObjId(stoneUniqueId);
				Item targetItem = player.getEquipment().getEquippedItemByObjId(targetItemUniqueId);
				if (targetItem == null) {
					targetItem = player.getInventory().getItemByObjId(targetItemUniqueId);
				}
				if (action.canAct(player, manastone, targetItem)) {
					Item supplement = player.getInventory().getItemByObjId(supplementUniqueId);
					if (supplement != null) {
						if (supplement.getItemId() / 100000 != 1661) { // suppliment id check
							return;
						}
					}
					action.act(player, manastone, targetItem, supplement, targetFusedSlot);
				}
				break;
			case 3: // remove manastone
				long price = PricesService.getPriceForService(500, player.getRace());
				if (player.getInventory().getKinah() < price) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(price));
					return;
				}
				if (obj != null && obj instanceof Npc && MathUtil.isInRange(player, obj, 7)) {
					player.getInventory().decreaseKinah(price);
					if (targetFusedSlot == 1)
						ItemSocketService.removeManastone(player, targetItemUniqueId, slotNum);
					else
						ItemSocketService.removeFusionstone(player, targetItemUniqueId, slotNum);
				}
		}
	}
}