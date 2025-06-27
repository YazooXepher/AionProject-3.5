package com.aionemu.gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.HousingFlags;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionWarehouse;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLASTIC_SURGERY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_REPURCHASE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SELL_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRADELIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRADE_IN_LIST;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.craft.RelinquishCraftStatus;
import com.aionemu.gameserver.services.instance.DredgionService2;
import com.aionemu.gameserver.services.item.ItemChargeService;
import com.aionemu.gameserver.services.teleport.PortalService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author VladimirZ
 */
public class DialogService {

	private static final Logger log = LoggerFactory.getLogger(DialogService.class);

	public static void onCloseDialog(Npc npc, Player player) {
		switch (npc.getObjectTemplate().getTitleId()) {
			case 350409:
			case 315073:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npc.getObjectId(), 0));
				Legion legion = player.getLegion();
				if (legion != null) {
					LegionWarehouse lwh = player.getLegion().getLegionWarehouse();
					if (lwh.getWhUser() == player.getObjectId()) {
						lwh.setWhUser(0);
					}
				}
				break;
		}
	}

	public static void onDialogSelect(int dialogId, final Player player, Npc npc, int questId, int extendedRewardIndex) {

		QuestEnv env = new QuestEnv(npc, player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env))
			return;

		if (player.getAccessLevel() >= 3 && CustomConfig.ENABLE_SHOW_DIALOGID) {
			PacketSendUtility.sendMessage(player, "dialogId: " + dialogId);
			PacketSendUtility.sendMessage(player, "questId: " + questId);
		}

		int targetObjectId = npc.getObjectId();
		int titleId = npc.getObjectTemplate().getTitleId();
		switch (dialogId) {
			case 2: {
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "Buy list is missing!!");
					break;
				}
				int tradeModifier = tradeListTemplate.getSellPriceRate();
				PacketSendUtility.sendPacket(player, new SM_TRADELIST(player, npc, tradeListTemplate, PricesService.getVendorBuyModifier()
					* tradeModifier / 100));
				break;
			}
			case 3: {
				PacketSendUtility.sendPacket(player, new SM_SELL_ITEM(targetObjectId, PricesService.getVendorSellModifier(player.getRace())));
				break;
			}
			case 4: { // stigma
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 1));
				break;
			}
			case 5: { // create legion
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 2));
				break;
			}
			case 6: { // disband legion
				LegionService.getInstance().requestDisbandLegion(npc, player);
				break;
			}
			case 7: { // recreate legion
				LegionService.getInstance().recreateLegion(npc, player);
				break;
			}
			case 21: { // warehouse (2.5)
				switch (titleId) {
					case 315008:
					case 350417:
					case 462878:
					case 0:
						if (!RestrictionsManager.canUseWarehouse(player))
							return;
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 26));
						WarehouseService.sendWarehouseInfo(player, true);
					break;
				}
				break;
			}
			case 26: {
				if (questId != 0) {
					QuestState qs = player.getQuestStateList().getQuestState(questId);
					if (qs != null) {
						if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) {
							if (AdminConfig.QUEST_DIALOG_LOG) {
								log
									.info("Error in the quest " + questId + ". No response from " + npc.getNpcId() + " on the step " + qs.getQuestVarById(0));
							}
							if (!"useitem".equals(npc.getAi2().getName())) {
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 10));
							}
							else {
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
							}
						}
					}
					else {
						if (AdminConfig.QUEST_DIALOG_LOG) {
							log.info("Quest " + questId + " is not implemented.");
						}
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 10));
					}
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
				}
				break;
			}
			case 28: { // Consign trade?? npc karinerk, koorunerk (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 13));
				break;
			}
			case 30: { // soul healing (2.5)
				final long expLost = player.getCommonData().getExpRecoverable();
				if (expLost == 0) {
					player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
					player.getCommonData().setDeathCount(0);
				}
				final double factor = (expLost < 1000000 ? 0.25 - (0.00000015 * expLost) : 0.1);
				final int price = (int) (expLost * factor);

				RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {

					@Override
					public void acceptRequest(Creature requester, Player responder) {
						if (player.getInventory().getKinah() >= price) {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GET_EXP2(expLost));
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SUCCESS_RECOVER_EXPERIENCE);
							player.getCommonData().resetRecoverableExp();
							player.getInventory().decreaseKinah(price);
							player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
							player.getCommonData().setDeathCount(0);
						}
						else {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(price));
						}
					}

					@Override
					public void denyRequest(Creature requester, Player responder) {
						// no message
					}
				};
				if (player.getCommonData().getExpRecoverable() > 0) {
					boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, responseHandler);
					if (result) {
						PacketSendUtility.sendPacket(player,
							new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, 0, 0, String.valueOf(price)));
					}
				}
				else {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DONOT_HAVE_RECOVER_EXPERIENCE);
				}
				break;
			}
			case 31: { // (2.5)
				switch (npc.getNpcId()) {
					case 204089: // pvp arena in pandaemonium.
						TeleportService2.teleportTo(player, 120010000, 1, 984f, 1543f, 222.1f);
						break;
					case 203764: // pvp arena in sanctum.
						TeleportService2.teleportTo(player, 110010000, 1, 1462.5f, 1326.1f, 564.1f);
						break;
					case 203981:
						TeleportService2.teleportTo(player, 210020000, 1, 439.3f, 422.2f, 274.3f);
						break;
				}
				break;
			}
			case 32: { // (2.5)
				switch (npc.getNpcId()) {
					case 204087:
						TeleportService2.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f);
						break;
					case 203875:
						TeleportService2.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f);
						break;
					case 203982:
						TeleportService2.teleportTo(player, 210020000, 1, 446.2f, 431.1f, 274.5f);
						break;
				}
				break;
			}
			case 36: { // Godstone socketing (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 21));
				break;
			}
			case 37: { // remove mana stone (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 20));
				break;
			}
			case 38: { // modify appearance (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 19));
				break;
			}
			case 39: { // flight and teleport (2.5)
				if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
					int level = player.getLevel();
					if (level < 9) {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
					}
					else {
						TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
					}
				}
				else {
					switch (npc.getNpcId()) {
						case 203194: {
							if (player.getRace() == Race.ELYOS) {
								QuestState qs = player.getQuestStateList().getQuestState(1006);
								if (qs == null || qs.getStatus() != QuestStatus.COMPLETE)
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
								else
									TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
							}
							break;
						}
						case 203679: {
							if (player.getRace() == Race.ASMODIANS) {
								QuestState qs = player.getQuestStateList().getQuestState(2008);
								if (qs == null || qs.getStatus() != QuestStatus.COMPLETE)
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
								else
									TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
							}
							break;
						}
						default: {
							TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
						}
					}
				}
				break;
			}
			case 40: // improve extraction (2.5)
			case 41: { // learn tailoring armor smithing etc. (2.5)
				CraftSkillUpdateService.getInstance().learnSkill(player, npc);
				break;
			}
			case 42: { // expand cube (2.5)
				CubeExpandService.expandCube(player, npc);
				break;
			}
			case 43: { // (2.5)
				WarehouseService.expandWarehouse(player, npc);
				break;
			}
			case 48: { // legion warehouse (2.5)
				switch (titleId) {
					case 350409:
					case 315073:
						LegionService.getInstance().openLegionWarehouse(player, npc);
						break;
				}
				break;
			}
			case 51: { // WTF??? Quest dialog packet (2.5)
				break;
			}
			case 53: { // (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 28));
				break;
			}
			case 54: { // coin reward (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 3, 0));
				// PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "This feature is not available yet",
				// ChatType.GOLDEN_YELLOW));
				break;
			}
			case 56:
			case 57: { // (2.5)
				byte changesex = 0; // 0 plastic surgery, 1 gender switch
				byte check_ticket = 2; // 2 no ticket, 1 have ticket
				if (dialogId == 57) {
					// Gender Switch
					changesex = 1;
					if (player.getInventory().getItemCountByItemId(169660000) > 0 || player.getInventory().getItemCountByItemId(169660001) > 0) {
						check_ticket = 1;
					}
				}
				else {
					// Plastic Surgery
					if (player.getInventory().getItemCountByItemId(169650000) > 0 || player.getInventory().getItemCountByItemId(169650001) > 0) {
						check_ticket = 1;
					}
				}
				PacketSendUtility.sendPacket(player, new SM_PLASTIC_SURGERY(player, check_ticket, changesex));
				player.setEditMode(true);
				break;
			}
			case 58: // dredgion
				if (AutoGroupConfig.AUTO_GROUP_ENABLE && DredgionService2.getInstance().isDredgionAvialable()) {
					AutoGroupType agt = AutoGroupType.getAutoGroup(npc.getNpcId());
					if (agt != null && agt.isDredgion()) {
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(agt.getInstanceMaskId()));
					}
					else {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
					}
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 1011));
				}
				break;
			case 60: { // (2.5)
				break;
			}
			case 61: { // armsfusion (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 29));
				break;
			}
			case 62: { // armsbreaking (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 30));
				break;
			}
			case 63: { // join npcFaction (2.5)
				player.getNpcFactions().enterGuild(npc);
				break;
			}
			case 64: { // leave npcFaction (2.5)
				player.getNpcFactions().leaveNpcFaction(npc);
				break;
			}
			case 65: { // repurchase (2.5)
				PacketSendUtility.sendPacket(player, new SM_REPURCHASE(player, npc.getObjectId()));
				break;
			}
			case 66: { // adopt pet (2.5)
				PacketSendUtility.sendPacket(player, new SM_PET(6));
				break;
			}
			case 67: { // surrender pet (2.5)
				PacketSendUtility.sendPacket(player, new SM_PET(7));
				break;
			}
			case 68: { // housing build
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 32));
				break;
			}
			case 69: { // housing destruct
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 33));
				break;
			}
			case 70: { // condition an individual item
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 35));
				break;
			}
			case 71: { // condition all equiped items
				ItemChargeService.startChargingEquippedItems(player, targetObjectId, 1);
				break;
			}
			case 73: {
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeInListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "Buy list is missing!!");
					break;
				}
				PacketSendUtility.sendPacket(player, new SM_TRADE_IN_LIST(npc, tradeListTemplate, 100));
				break;
			}
			case 74: { // relinquish Expert Status
				RelinquishCraftStatus.relinquishExpertStatus(player, npc);
				break;
			}
			case 75: { // relinquish Master Status
				RelinquishCraftStatus.relinquishMasterStatus(player, npc);
				break;
			}
			case 79: { // housing auction
				if ((player.getHousingStatus() & HousingFlags.BIDDING_ALLOWED.getId()) == 0) {
					if (player.getRace() == Race.ELYOS)
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(18802));
					else
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(28802));
					return;
				}
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 38));
				break;
			}
			case 87:
				PacketSendUtility.sendPacket(player, new SM_PET(16));
				break;
			case 88:
				PacketSendUtility.sendPacket(player, new SM_PET(17));
				break;
			case 89: // augmenting an individual item
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 42));
				break;
			case 90: // augmenting all equiped items
				ItemChargeService.startChargingEquippedItems(player, targetObjectId, 2);
				break;
			case 91: // recreate personal house instance (studio)
				HousingService.getInstance().recreatePlayerStudio(player);
				break;
			case 95: // town improvement
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 43));
				break;
			case 10000:
			case 10001:
			case 10002:
				if (questId == 0) { // generic npc reply (most are teleporters)
					TeleporterTemplate template = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npc.getNpcId());
					PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(npc.getNpcId(), dialogId, player.getRace());
					if (portalPath != null) {
						PortalService.port(portalPath, player, targetObjectId);
					}
					else if (template != null) {
						TeleportLocation loc = template.getTeleLocIdData().getTelelocations().get(0);
						if (loc != null) {
							TeleportService2.teleport(template, loc.getLocId(), player, npc,
								npc.getAi2().getName().equals("general") ? TeleportAnimation.JUMP_AIMATION : TeleportAnimation.BEAM_ANIMATION);
						}
					}
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
				}
				break;
			default: {
				if (questId > 0) {
					if (dialogId == 18 && player.getInventory().isFull())
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
					else
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId));
				}
				break;
			}
		}
	}
}