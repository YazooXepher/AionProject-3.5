package com.aionemu.gameserver.services.item;

import java.util.Collection;
import java.util.Collections;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.items.ChargeInfo;
import com.aionemu.gameserver.model.templates.item.Improvement;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author ATracer
 */
public class ItemChargeService {

	/**
	 * @return collection of items for conditioning
	 */
	public static Collection<Item> filterItemsToCondition(Player player, Item selectedItem, final int chargeWay) {
		if (selectedItem != null) {
			return Collections.singletonList(selectedItem);
		}
		return Collections2.filter(player.getEquipment().getEquippedItems(), new Predicate<Item>() {

			@Override
			public boolean apply(Item item) {
				return item.getChargeLevelMax() != 0 && item.getImprovement() != null
						&& item.getImprovement().getChargeWay() == chargeWay
						&& item.getChargePoints() < ChargeInfo.LEVEL2;
			}
		});
	}

	public static void startChargingEquippedItems(final Player player, int senderObj, final int chargeWay) {
		// TODO: Check this : SM_QUESTION_WINDOW.STR_ITEM_CHARGE_CONFIRM_SOME_ALREADY_CHARGED !!!
		final Collection<Item> filteredItems = filterItemsToCondition(player, null, chargeWay);
		if (filteredItems.isEmpty()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(chargeWay == 1 ? 1400895 : 1401343));
			return;
		}

		final long payAmount = calculatePrice(filteredItems);
		
		RequestResponseHandler request = new RequestResponseHandler(player) {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (processPayment(player, chargeWay, payAmount)) {
					for (Item item : filteredItems) {
						chargeItem(player, item, item.getChargeLevelMax());
					}
				}
			}

			@Override
			public void denyRequest(Creature requester, Player responder) {
				// Nothing Happens
			}

		};
		int msg = chargeWay == 1 ? SM_QUESTION_WINDOW.STR_ITEM_CHARGE_ALL_CONFIRM
				: SM_QUESTION_WINDOW.STR_ITEM_CHARGE2_ALL_CONFIRM;
		if (player.getResponseRequester().putRequest(msg, request))
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(msg,
				senderObj, 0, String.valueOf(payAmount)));
	}
	
	private static long calculatePrice(Collection<Item> items) {
		long result = 0;
		for (Item item : items) {
			result += getPayAmountForService(item, item.getChargeLevelMax());
		}
		return result;
	}

	public static void chargeItems(Player player, Collection<Item> items, int level) {
		for (Item item : items) {
			chargeItem(player, item, level);
		}
	}

	public static void chargeItem(Player player, Item item, int level) {
		Improvement improvement = item.getImprovement();
		if (improvement == null) {
			return;
		}
		int chargeWay = improvement.getChargeWay();
		int currentCharge = item.getChargePoints();
		switch (level) {
			case 1:
				item.getConditioningInfo().updateChargePoints(ChargeInfo.LEVEL1 - currentCharge);
				break;
			case 2:
				item.getConditioningInfo().updateChargePoints(ChargeInfo.LEVEL2 - currentCharge);
				break;
		}
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, ItemUpdateType.CHARGE));
		player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
		if (chargeWay == 1) {
			PacketSendUtility.sendPacket(player,
				SM_SYSTEM_MESSAGE.STR_MSG_ITEM_CHARGE_SUCCESS(new DescriptionId(item.getNameID()), level));
		}
		else {
			PacketSendUtility.sendPacket(player,
				SM_SYSTEM_MESSAGE.STR_MSG_ITEM_CHARGE2_SUCCESS(new DescriptionId(item.getNameID()), level));
		}
		player.getGameStats().updateStatsVisually();
	}

	/**
	 * Pay for conditioning of item
	 */
	public static boolean processPayment(Player player, Item item, int level) {
		return processPayment(player, item.getImprovement().getChargeWay(), getPayAmountForService(item, level));
	}
	
	public static boolean processPayment(Player player, int chargeWay, long amount) {
		switch (chargeWay) {
			case 1:
				return processKinahPayment(player, amount);
			case 2:
				return processAPPayment(player, amount);
		}
		return false;
	}
	
	public static boolean processKinahPayment(Player player, long requiredKinah) {
		if (player.getInventory().getKinah() < requiredKinah) {
			return false;
		}
		player.getInventory().decreaseKinah(requiredKinah);
		return true;
	}
	
	public static boolean processAPPayment(Player player, long requiredAP) {
		if (player.getAbyssRank().getAp() < requiredAP) {
			return false;
		}
		AbyssPointsService.addAp(player, (int) -requiredAP);
		return true;
	}

	public static long getPayAmountForService(Item item, int chargeLevel) {
		Improvement improvement = item.getImprovement();
		if (improvement == null)
			return 0;

		switch (chargeLevel) {
			case 1:
				return improvement.getPrice1() / 2;
			case 2:
				switch (getNextChargeLevel(item)) {
					case 1:
						return (improvement.getPrice1() + improvement.getPrice2());
					case 2:
						return improvement.getPrice2();
				}
		}
		return 0;
	}

	private static int getNextChargeLevel(Item item) {
		int charge = item.getChargePoints();
		if (charge < ChargeInfo.LEVEL1) {
			return 1;
		}
		if (charge < ChargeInfo.LEVEL2) {
			return 2;
		}
		throw new IllegalArgumentException("Invalid charge level " + charge);
	}
}