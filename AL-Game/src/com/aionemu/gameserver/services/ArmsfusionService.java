package com.aionemu.gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class is responsible of Armsfusion-related tasks (fusion,breaking)
 * 
 * @author Wakizashi modified by Source & xTz
 */
public class ArmsfusionService {

	private static final Logger log = LoggerFactory.getLogger(ArmsfusionService.class);

	public static void fusionWeapons(Player player, int firstItemUniqueId, int secondItemUniqueId) {
		Item firstItem = player.getInventory().getItemByObjId(firstItemUniqueId);
		if (firstItem == null)
			firstItem = player.getEquipment().getEquippedItemByObjId(firstItemUniqueId);

		Item secondItem = player.getInventory().getItemByObjId(secondItemUniqueId);
		if (secondItem == null)
			secondItem = player.getEquipment().getEquippedItemByObjId(secondItemUniqueId);

		/*
		 * Check if item is in bag
		 */
		if (firstItem == null || secondItem == null || !(player.getTarget() instanceof Npc))
			return;

		double priceRate = PricesService.getGlobalPrices(player.getRace()) * .01;
		double taxRate = PricesService.getTaxes(player.getRace()) * .01;
		double rarity = rarityRate(firstItem.getItemTemplate().getItemQuality());
		int priceMod = PricesService.getGlobalPricesModifier() * 2;
		int level = firstItem.getItemTemplate().getLevel();

		int price = (int) (priceMod * priceRate * taxRate * rarity * level * level);
		log.debug("Rarete: " + rarity + " Prix Ratio: " + priceRate + " Tax: " + taxRate + " Mod: " + priceMod
			+ " NiveauDeLArme: " + level);
		log.debug("Prix: " + price);

		if (player.getInventory().getKinah() < price) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_ENOUGH_MONEY(firstItem.getNameID(), secondItem.getNameID()));
			return;
		}

		/*
		 * Fusioned weapons must be not fusioned
		 */
		if (firstItem.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_AVAILABLE(firstItem.getNameID()));
			return;
		}
		if (secondItem.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_AVAILABLE(secondItem.getNameID()));
			return;
		}

		if (!firstItem.getItemTemplate().isCanFuse() || !secondItem.getItemTemplate().isCanFuse()) {
			PacketSendUtility.sendMessage(player, "You performed illegal operation, admin will catch you");
			log.info("[AUDIT] Client hack with item fusion, player: " + player.getName());
			return;
		}

		if (!firstItem.getItemTemplate().isTwoHandWeapon()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_AVAILABLE(firstItem.getNameID()));
			return;
		}

		// Fusioned weapons must have same type
		if (firstItem.getItemTemplate().getWeaponType() != secondItem.getItemTemplate().getWeaponType()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_DIFFERENT_TYPE);
			return;
		}

		/*
		 * Second weapon must have inferior or equal lvl. in relation to first weapon
		 */
		if (secondItem.getItemTemplate().getLevel() > firstItem.getItemTemplate().getLevel()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_MAIN_REQUIRE_HIGHER_LEVEL);
			return;
		}
		
		//You can not combine Conditioning and Augmenting
		if (firstItem.getImprovement() != null && secondItem.getImprovement() != null) {
			if (firstItem.getImprovement().getChargeWay() != secondItem.getImprovement().getChargeWay()) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_COMPARABLE_ITEM);
				return;
			}
		}

		firstItem.setFusionedItem(secondItem.getItemTemplate());

		ItemSocketService.removeAllFusionStone(player, firstItem);

		if (secondItem.hasOptionalSocket())
			firstItem.setOptionalFusionSocket(secondItem.getOptionalSocket());
		else
			firstItem.setOptionalFusionSocket(0);

		ItemSocketService.copyFusionStones(secondItem, firstItem);

		DAOManager.getDAO(InventoryDAO.class).store(firstItem, player);

		if (!player.getInventory().decreaseByObjectId(secondItemUniqueId, 1))
			return;

		ItemPacketService.updateItemAfterInfoChange(player, firstItem);
		player.getInventory().decreaseKinah(price);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_SUCCESS(firstItem.getNameID(), secondItem.getNameID()));
	}

	private static double rarityRate(ItemQuality rarity) {
		switch (rarity) {
			case COMMON:
				return 1.0;
			case RARE:
				return 1.25;
			case LEGEND:
				return 1.5;
			case UNIQUE:
				return 2.0;
			case EPIC:
				return 2.5;
			default:
				return 1.0;
		}
	}

	public static void breakWeapons(Player player, int weaponToBreakUniqueId) {
		Item weaponToBreak = player.getInventory().getItemByObjId(weaponToBreakUniqueId);
		if (weaponToBreak == null)
			weaponToBreak = player.getEquipment().getEquippedItemByObjId(weaponToBreakUniqueId);

		if (weaponToBreak == null || !(player.getTarget() instanceof Npc))
			return;

		if (!weaponToBreak.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOUND_ERROR_NOT_AVAILABLE(weaponToBreak.getNameID()));
			return;
		}

		weaponToBreak.setFusionedItem(null);
		ItemSocketService.removeAllFusionStone(player, weaponToBreak);
		DAOManager.getDAO(InventoryDAO.class).store(weaponToBreak, player);

		ItemPacketService.updateItemAfterInfoChange(player, weaponToBreak);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUNDED_ITEM_DECOMPOUND_SUCCESS(weaponToBreak.getNameID()));
	}
}