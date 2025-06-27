package com.aionemu.gameserver.services.toypet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PetAdoptionService {

	private static final Logger log = LoggerFactory.getLogger(PetAdoptionService.class);

	/**
	 * Create a pet for player (with validation)
	 * 
	 * @param player
	 * @param eggObjId
	 * @param petId
	 * @param name
	 * @param decorationId
	 */
	public static final void adoptPet(Player player, int eggObjId, int petId, String name, int decorationId) {

		if (!validateEgg(player, eggObjId, petId)) {
			return;
		}
		addPet(player, petId, name, decorationId);
	}

	/**
	 * Add pet to player
	 * 
	 * @param player
	 * @param petId
	 * @param name
	 * @param decorationId
	 */
	public static void addPet(Player player, int petId, String name, int decorationId) {
		if (player.getPetList().hasPet(petId)) {
			log.warn("Duplicate pet adoption");
			return;
		}
        if (DataManager.PET_DATA.getPetTemplate(petId) == null) {
          log.warn("Trying adopt pet without template. PetId:" + petId);
          return;
        }
		PetCommonData petCommonData = player.getPetList().addPet(player, petId, decorationId, name);
		if (petCommonData != null) {
			PacketSendUtility.sendPacket(player, new SM_PET(1, petCommonData));
		}
	}

	private static boolean validateEgg(Player player, int eggObjId, int petId) {
		int eggId = player.getInventory().getItemByObjId(eggObjId).getItemId();
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(eggId);
		if (template == null || template.getActions() == null || template.getActions().getAdoptPetAction() == null ||
			template.getActions().getAdoptPetAction().getPetId() != petId) {
			return false;
		}
		return player.getInventory().decreaseByObjectId(eggObjId, 1);
	}

	/**
	 * Delete pet
	 * 
	 * @param player
	 * @param petId
	 */
	public static final void surrenderPet(Player player, int petId) {
		PetCommonData petCommonData = player.getPetList().getPet(petId);
		if (player.getPet() != null && player.getPet().getPetId() == petCommonData.getPetId()) {
			if (petCommonData.getFeedProgress() != null)
				petCommonData.setCancelFeed(true);
			PetSpawnService.dismissPet(player, false);
		}
		player.getPetList().deletePet(petCommonData.getPetId());
		PacketSendUtility.sendPacket(player, new SM_PET(2, petCommonData));
	}
}