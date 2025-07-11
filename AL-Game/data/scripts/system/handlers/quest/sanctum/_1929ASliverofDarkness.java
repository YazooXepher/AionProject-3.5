package quest.sanctum;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author Mr. Poke
 * @modified Rolandas
 * @reworked vlog
 */
public class _1929ASliverofDarkness extends QuestHandler {

	private final static int questId = 1929;

	public _1929ASliverofDarkness() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203752, 203852, 203164, 205110, 700240, 205111, 203701, 203711 };
		int[] stigmas = { 140000008, 140000027, 140000047, 140000076, 140000131, 140000147, 140000098, 140000112 };
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(155, questId);
		qe.registerQuestNpc(212992).addOnKillEvent(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnDie(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		for (int stigma : stigmas) {
			qe.registerOnEquipItem(stigma, questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		int var = qs.getQuestVars().getQuestVars();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203752: { // Jucleas
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 203852: { // Ludina
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
					}
					break;
				}
				case 203164: { // Morai
					switch (dialog) {
						case START_DIALOG: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 8) {
								return sendQuestDialog(env, 3057);
							}
						}
						case STEP_TO_3: {
							if (var == 2) {
								changeQuestStep(env, 2, 93, false); // 93
								WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310070000);
								InstanceService.registerPlayerWithInstance(newInstance, player);
								TeleportService2.teleportTo(player, 310070000, newInstance.getInstanceId(), 338, 101, 1191);
								return closeDialogWindow(env);
							}
						}
						case STEP_TO_7: {
							return defaultCloseDialog(env, 8, 9); // 9
						}
					}
					break;
				}
				case 205110: { // Icaronix
					switch (dialog) {
						case START_DIALOG: {
							if (var == 93) {
								return sendQuestDialog(env, 2034);
							}
						}
						case STEP_TO_4: {
							if (var == 93) {
								changeQuestStep(env, 93, 94, false); // 94
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(31001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 31001, 0));
								return true;
							}
						}
					}
					break;
				}
				case 700240: { // Icaronix's Box
					if (dialog == QuestDialog.USE_OBJECT) {
						if (var == 94) {
							return playQuestMovie(env, 155);
						}
					}
					break;
				}
				case 205111: { // Ecus
					switch (dialog) {
						case USE_OBJECT: {
							if (var == 96) {
								if (isStigmaEquipped(env)) {
									return sendQuestDialog(env, 2716);
								}
								else {
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1));
									return closeDialogWindow(env);
								}
							}
						}
						case START_DIALOG: {
							if (var == 98) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SELECT_ACTION_2546: {
							if (var == 98) {
								if (giveQuestItem(env, getStoneId(player), 1)) {
									long existendStigmaShards = player.getInventory().getItemCountByItemId(141000001);
									if (existendStigmaShards < 60) {
										if (!player.getInventory().isFull()) {
											ItemService.addItem(player, 141000001, 60 - existendStigmaShards);
											PacketSendUtility.sendPacket(player,
												new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1));
											return true;
										}
									}
									else {
										PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1));
										return true;
									}
								}
							}
						}
						case SELECT_ACTION_2720: {
							if (var == 96) {
								Npc npc = (Npc) env.getVisibleObject();
								npc.getController().onDelete();
								QuestService.addNewSpawn(310070000, player.getInstanceId(), 212992, (float) 191.9, (float) 267.68,
									(float) 1374, (byte) 0);
								changeQuestStep(env, 96, 97, false); // 97
								return closeDialogWindow(env);
							}
						}
					}
					break;
				}
				case 203701: { // Lavirintos
					switch (dialog) {
						case START_DIALOG: {
							if (var == 9) {
								return sendQuestDialog(env, 3398);
							}
						}
						case STEP_TO_8: {
							return defaultCloseDialog(env, 9, 9, true, false); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203711) { // Miriya
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
		if (movieId == 155) {
			QuestService.addNewSpawn(310070000, player.getInstanceId(), 205111, (float) 197.6, (float) 265.9, (float) 1374.0,
				(byte) 0);
			changeQuestStep(env, 94, 98, false); // 98
			return true;
		}
		return false;
	}

	@Override
	public boolean onEquipItemEvent(QuestEnv env, int itemId) {
		changeQuestStep(env, 98, 96, false); // 96
		return closeDialogWindow(env);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 97) {
				changeQuestStep(env, 97, 8, false); // 8
				TeleportService2.teleportTo(player, 210030000, 1, 2315.9f, 1800f, 195.2f);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 93 && var <= 98) {
				removeStigma(env);
				qs.setQuestVar(2);
				updateQuestStatus(env);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
					DataManager.QUEST_DATA.getQuestById(questId).getName()));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (player.getWorldId() != 310070000) {
				if (var >= 93 && var <= 98) {
					removeStigma(env);
					qs.setQuestVar(2);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
				else if (var == 8) {
					removeStigma(env);
					return true;
				}
			}
		}
		return false;
	}

	private int getStoneId(Player player) {
		switch (player.getCommonData().getPlayerClass()) {
			case GLADIATOR: {
				return 140000008;// Improved Stamina I
			}
			case TEMPLAR: {
				return 140000027;// Divine Fury I
			}
			case RANGER: {
				return 140000047;// Arrow Deluge I
			}
			case ASSASSIN: {
				return 140000076;// Sigil Strike I
			}
			case SORCERER: {
				return 140000131;// Lumiel's Wisdom I
			}
			case SPIRIT_MASTER: {
				return 140000147;// Absorb Vitality I
			}
			case CLERIC: {
				return 140000098;// Grace of Empyrean Lord I
			}
			case CHANTER: {
				return 140000112;// Rage Spell I
			}
			default: {
				return 0;
			}
		}
	}

	private boolean isStigmaEquipped(QuestEnv env) {
		Player player = env.getPlayer();
		for (Item i : player.getEquipment().getEquippedItemsAllStigma()) {
			if (i.getItemId() == getStoneId(player)) {
				return true;
			}
		}
		return false;
	}

	private void removeStigma(QuestEnv env) {
		Player player = env.getPlayer();
		for (Item item : player.getEquipment().getEquippedItemsByItemId(getStoneId(player))) {
			player.getEquipment().unEquipItem(item.getObjectId(), 0);
		}
		removeQuestItem(env, getStoneId(player), 1);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
}