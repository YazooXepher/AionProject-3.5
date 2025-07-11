package quest.sarpan;

import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author vlog
 */
public class _10051UnwelcomeMessengers extends QuestHandler {

	private static final int questId = 10051;

	public _10051UnwelcomeMessengers() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcIds = { 205581, 205535, 205765, 205988 };
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterWorld(questId);
		for (int npcId : npcIds) {
			qe.registerQuestNpc(npcId).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10050, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 205581: { // Kutos
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
				case 205535: { // Killios
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
				case 205765: { // Philmoni
					switch (dialog) {
						case START_DIALOG: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case CHECK_COLLECTED_ITEMS: {
							return checkQuestItems(env, 3, 4, false, 10000, 10001); // 4
						}
						case STEP_TO_3: {
							return defaultCloseDialog(env, 2, 3); // 3
						}
						case FINISH_DIALOG: {
							return closeDialogWindow(env);
						}
					}
					break;
				}
				case 730465: { // Mysterious Orb
					switch (dialog) {
						case USE_OBJECT: {
							if (var == 4) {
								TeleportService2.teleportTo(player, 300390000, 273.96478f, 217.55084f, 207.5269f, (byte) 60,
									TeleportAnimation.BEAM_ANIMATION);
								return true;
							}
						}
					}
					break;
				}
				case 205988: { // Ispharel
					switch (dialog) {
						case START_DIALOG: {
							if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SET_REWARD: {
							return defaultCloseDialog(env, 4, 4, true, false); // reward
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205535) { // Killios
				if (dialog == QuestDialog.USE_OBJECT) {
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
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (player.getWorldId() == 300390000) {
				if (var == 4) {
					return playQuestMovie(env, 702);
				}
			}
		}
		return false;
	}
}