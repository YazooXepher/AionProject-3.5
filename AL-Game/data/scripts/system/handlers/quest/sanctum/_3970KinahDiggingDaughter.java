package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Cheatkiller
 *
 */
public class _3970KinahDiggingDaughter extends QuestHandler {

	private final static int questId = 3970;

	public _3970KinahDiggingDaughter() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(203893).addOnQuestStart(questId);
		qe.registerQuestNpc(798072).addOnTalkEvent(questId);
		qe.registerQuestNpc(279020).addOnTalkEvent(questId);
		qe.registerQuestNpc(798053).addOnTalkEvent(questId);
		qe.registerQuestNpc(798386).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203893) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env, 182206112, 1);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798072) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1352);
					}
				}
				else if (dialog == QuestDialog.STEP_TO_1) {
					removeQuestItem(env, 182206112, 1);
					giveQuestItem(env, 182206113, 1);
					return defaultCloseDialog(env, 0, 1);
				}
			}
			if (targetId == 279020) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 1) {
						return sendQuestDialog(env, 1693);
					}
				}
				else if (dialog == QuestDialog.STEP_TO_2) {
					removeQuestItem(env, 182206113, 1);
					giveQuestItem(env, 182206114, 1);
					return defaultCloseDialog(env, 1, 2);
				}
			}
			if (targetId == 798053) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 2) {
						return sendQuestDialog(env, 2034);
					}
				}
				else if (dialog == QuestDialog.STEP_TO_3) {
					removeQuestItem(env, 182206114, 1);
					giveQuestItem(env, 182206115, 1);
					qs.setQuestVar(3);
					return defaultCloseDialog(env, 3, 3, true, false);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798386) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				}
				removeQuestItem(env, 182206115, 1);
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}