package quest.chantra_dredgion;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _3725MyLuckyNumber extends QuestHandler {

	private static final int questId = 3725;

	public _3725MyLuckyNumber() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnDredgionReward(questId);
		qe.registerQuestNpc(799069).addOnQuestStart(questId);
		qe.registerQuestNpc(799069).addOnTalkEvent(questId);
		qe.registerQuestNpc(798928).addOnTalkEvent(questId);
		qe.registerQuestNpc(281866).addOnKillEvent(questId);
		qe.registerQuestNpc(216866).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 799069) { // Yannis
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			if (targetId == 798928) { // Yulia
				if (dialog == QuestDialog.START_DIALOG) {
					if (var1 == 6 && var2 == 15) {
						return sendQuestDialog(env, 10002);
					}
				}
				else if (dialog == QuestDialog.SELECT_REWARD) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 5);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798928) { // Yulia
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		int[] mobs = { 281866, 216866 };
		return defaultOnKillEvent(env, mobs, 0, 15, 2); // 2: 0 - 15
	}

	@Override
	public boolean onDredgionRewardEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var1 = qs.getQuestVarById(1);
			if (var1 < 6) {
				changeQuestStep(env, var1, var1 + 1, false, 1); // 1: 0 - 6
				return true;
			}
		}
		return false;
	}
}