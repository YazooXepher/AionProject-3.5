package com.aionemu.gameserver.controllers;

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.observer.StartMovingListener;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.skillengine.task.GatheringTask;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.captcha.CAPTCHAUtil;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer, sphinx, Cura
 */
public class GatherableController extends VisibleObjectController<Gatherable> {

	private int gatherCount;

	private int currentGatherer;

	private GatheringTask task;

	public enum GatherState {
		GATHERED,
		GATHERING,
		IDLE
	}

	private GatherState state = GatherState.IDLE;

	/**
	 * Start gathering process
	 * 
	 * @param player
	 */
	public void onStartUse(final Player player) {
		// basic actions, need to improve here
		final GatherableTemplate template = this.getOwner().getObjectTemplate();
		if (template.getLevelLimit() > 0) {
			// You must be at least level %0 to perform extraction.
			if (player.getLevel() < template.getLevelLimit()) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400737, template.getLevelLimit()));
				return;
			}
		}

		if (player.getInventory().isFull()) {
			// You must have at least one free space in your cube to gather.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330036));
			return;
		}
		if (MathUtil.getDistance(getOwner(), player) > 6)
			return;

		// check is gatherable
		if (!checkGatherable(player, template))
			return;

		if (!checkPlayerSkill(player, template))
			return;

		// check for extractor in inventory
		byte result = checkPlayerRequiredExtractor(player, template);
		if (result == 0)
			return;

		// CAPTCHA
		if (SecurityConfig.CAPTCHA_ENABLE) {
			if (SecurityConfig.CAPTCHA_APPEAR.equals(template.getSourceType()) || SecurityConfig.CAPTCHA_APPEAR.equals("ALL")) {
				int rate = SecurityConfig.CAPTCHA_APPEAR_RATE;
				if (template.getCaptchaRate() > 0)
					rate = (int) (template.getCaptchaRate() * 0.1f);

				if (Rnd.get(0, 100) < rate) {
					player.setCaptchaWord(CAPTCHAUtil.getRandomWord());
					player.setCaptchaImage(CAPTCHAUtil.createCAPTCHA(player.getCaptchaWord()).array());
					PunishmentService.setIsNotGatherable(player, 0, true, SecurityConfig.CAPTCHA_EXTRACTION_BAN_TIME * 1000L);
				}
			}
		}

		List<Material> materials = null;
		switch (result) {
			case 1: // player has equipped item, or have a consumable in inventory, so he will obtain extra items
				materials = template.getExtraMaterials().getMaterial();
				break;
			case 2:// regular thing
				materials = template.getMaterials().getMaterial();
				break;
		}

		int chance = Rnd.get(10000000);
		int current = 0;
		Material curMaterial = null;
		for (Material mat : materials) {
			current += mat.getRate();
			if (current >= chance) {
				curMaterial = mat;
				break;
			}
		}

		synchronized (state) {
			if (state != GatherState.GATHERING) {
				state = GatherState.GATHERING;
				currentGatherer = player.getObjectId();
				player.getObserveController().attach(new StartMovingListener() {

					@Override
					public void moved() {
						finishGathering(player);
					}
				});
				int skillLvlDiff = player.getSkillList().getSkillLevel(template.getHarvestSkill()) - template.getSkillLevel();
				task = new GatheringTask(player, getOwner(), curMaterial, skillLvlDiff);
				task.start();
			}
		}
	}

	/**
	 * Checks whether player have needed skill for gathering and skill level is sufficient
	 * 
	 * @param player
	 * @param template
	 * @return
	 */
	private boolean checkPlayerSkill(final Player player, final GatherableTemplate template) {
		int harvestSkillId = template.getHarvestSkill();
		if (!player.getSkillList().isSkillPresent(harvestSkillId)) {
			if (harvestSkillId == 30001) {
				//You are Daeva now, leave this to humans.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GATHER_INCORRECT_SKILL);
			}
			else {
				// You must learn the %0 skill to start gathering.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330054, new DescriptionId(DataManager.SKILL_DATA
					.getSkillTemplate(harvestSkillId).getNameId())));
			}
			return false;
		}
		if (player.getSkillList().getSkillLevel(harvestSkillId) < template.getSkillLevel()) {
			// Your %0 skill level is not high enough.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330001, new DescriptionId(DataManager.SKILL_DATA
				.getSkillTemplate(harvestSkillId).getNameId())));
			return false;
		}
		return true;
	}

	private byte checkPlayerRequiredExtractor(final Player player, final GatherableTemplate template) {
		if (template.getRequiredItemId() > 0) {
			if (template.getCheckType() == 1) {
				List<Item> items = player.getEquipment().getEquippedItemsByItemId(template.getRequiredItemId());
				boolean condOk = false;
				for (Item item : items) {
					if (item.isEquipped()) {
						condOk = true;
						break;
					}
				}
				return (byte) (condOk ? 1 : 2);
			
			}
			else if (template.getCheckType() == 2) {
				if (player.getInventory().getItemCountByItemId(template.getRequiredItemId()) < template.getEraseValue()){
					// You do not have enough %0 to gather.
					PacketSendUtility.sendPacket(player,
						new SM_SYSTEM_MESSAGE(1400376, new DescriptionId(template.getRequiredItemNameId())));
					return 0;
				}
				else
					return 1;
			}
		}

		return 2;
	}

	/**
	 * @param player
	 * @param template
	 * @return
	 * @author Cura
	 */
	private boolean checkGatherable(final Player player, final GatherableTemplate template) {
		if (player.isNotGatherable()) {
			// You are currently poisoned and unable to extract. (Time remaining: %DURATIONTIME0)
			PacketSendUtility.sendPacket(
				player,
				new SM_SYSTEM_MESSAGE(1400273, (int) ((player.getGatherableTimer() - (System.currentTimeMillis() - player
					.getStopGatherable())) / 1000)));
			return false;
		}
		return true;
	}

	public void completeInteraction() {
		state = GatherState.IDLE;
		gatherCount++;
		if (gatherCount == getOwner().getObjectTemplate().getHarvestCount()) {
			onDespawn();
		}
	}

	public void rewardPlayer(Player player) {
		if (player != null) {
			int skillLvl = getOwner().getObjectTemplate().getSkillLevel();
			int xpReward = (int) ((0.0031 * (skillLvl + 5.3) * (skillLvl + 1592.8) + 60));

			if (player.getSkillList().addSkillXp(player, getOwner().getObjectTemplate().getHarvestSkill(),
				(int) RewardType.GATHERING.calcReward(player, xpReward), skillLvl)) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_EXTRACT_GATHERING_SUCCESS_GETEXP);
				player.getCommonData().addExp(xpReward, RewardType.GATHERING);
			}
			else
				PacketSendUtility.sendPacket(
					player,
					SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(
							new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(getOwner().getObjectTemplate().getHarvestSkill()).getNameId())));
		}
	}

	/**
	 * Called by client when some action is performed or on finish gathering Called by move observer on player move
	 * 
	 * @param player
	 */
	public void finishGathering(Player player) {
		if (currentGatherer == player.getObjectId()) {
			if (state == GatherState.GATHERING) {
				task.abort();
			}
			currentGatherer = 0;
			state = GatherState.IDLE;
		}
	}

	@Override
	public void onDespawn() {
		Gatherable owner = getOwner();
		if (!getOwner().isInInstance()) {
			RespawnService.scheduleRespawnTask(owner);
		}
		World.getInstance().despawn(owner);
	}

	@Override
	public void onBeforeSpawn() {
		this.gatherCount = 0;
	}

	@Override
	public Gatherable getOwner() {
		return super.getOwner();
	}
}