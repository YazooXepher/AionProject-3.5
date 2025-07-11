package quest.beluslan;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author Gigi
 */
public class _28602IntotheUnknown extends QuestHandler {

	private final static int questId = 28602;
	private final static int[] npc_ids = { 205234, 700939 };

	public _28602IntotheUnknown() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterWorld(questId);
		qe.registerOnDie(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		qe.registerQuestNpc(205234).addOnQuestStart(questId);
		qe.registerQuestNpc(217006).addOnKillEvent(questId);
		qe.registerQuestNpc(700939).addOnAtDistanceEvent(questId);
		qe.registerOnMovieEndQuest(454, questId);
	}
	
	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() != 300230000) {
				int var = qs.getQuestVarById(0);
				if (var > 0) {
					changeQuestStep(env, var, 0, false);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var > 0) {
				changeQuestStep(env, var, 0, false);
				return true;
			}
		}
		return false;
	}

	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			return defaultOnKillEvent(env, 217006, 3, true);
		}
		return false;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId != 454)
			return false;
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		changeQuestStep(env, 1, 2, false);
		return true;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 205234) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 205234) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					if (!player.getPortalCooldownList().isPortalUseDisabled(300230000)) {
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300230000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService2.teleportTo(player, 300230000, newInstance.getInstanceId(), 244.98566f, 244.14162f,
							189.52058f, (byte) 30);
						player.getPortalCooldownList().addPortalCooldown(300230000, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, newInstance.getMapId()));
						changeQuestStep(env, 0, 1, false); // 1
					}
					return closeDialogWindow(env);
				}
			}
			else if (targetId == 700939) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					if (var == 2) {
						return sendQuestDialog(env, 1693);
					}
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					return defaultCloseDialog(env, 2, 3);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205234) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onAtDistanceEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.COMPLETE)) {
			if (!player.getEffectController().hasAbnormalEffect(19288)) {
				SkillEngine.getInstance().applyEffectDirectly(19288, player, player, 0);
				return true;
			}
		}
		return false;
	}
}