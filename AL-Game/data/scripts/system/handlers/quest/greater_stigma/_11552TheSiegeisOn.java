package quest.greater_stigma;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 *
 */
public class _11552TheSiegeisOn extends QuestHandler {

	private final static int questId = 11552;
	
	public _11552TheSiegeisOn() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205531).addOnQuestStart(questId);
		qe.registerQuestNpc(205531).addOnTalkEvent(questId);
		qe.registerQuestNpc(259014).addOnAttackEvent(questId);
		qe.registerQuestNpc(259214).addOnAttackEvent(questId);
		qe.registerQuestNpc(259414).addOnAttackEvent(questId);
		qe.registerQuestNpc(259614).addOnAttackEvent(questId);
	}
	
	@Override
	public boolean onKillInWorldEvent(QuestEnv env) {
		return defaultOnKillRankedEvent(env, 0, 10, false); // reward
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		
		if (env.getTargetId() == 205531) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
				switch (dialog) {
					case START_DIALOG:
						return sendQuestDialog(env, 4762);
					case ACCEPT_QUEST_SIMPLE:
						return sendQuestStartDialog(env);
				}
			}
			else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1352);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onAttackEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 0 && env.getTargetId() == 259014) {
				changeQuestStep(env, var, var + 1, false);
				return true;
			}else if(var == 1 && env.getTargetId() == 259214){
				changeQuestStep(env, var, var + 1, false);
				return true;
			}else if(var == 2 && env.getTargetId() == 259414){
				changeQuestStep(env, var, var + 1, false);
				return true;
			}else if(var == 3 && env.getTargetId() == 259614){
				changeQuestStep(env, var, var + 1, true);
				return true;
			}
		}
		return false;
	}
}