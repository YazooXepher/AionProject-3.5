package quest.tiamaranta;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author mr.madison
 *
 */
public class _41509TryandCatchtheWind extends QuestHandler {

	private final static int questId = 41509;

	public _41509TryandCatchtheWind() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(205909).addOnQuestStart(questId);
		qe.registerQuestNpc(205936).addOnTalkEvent(questId);
		qe.registerQuestNpc(218215).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205909) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					}
					case ACCEPT_QUEST_SIMPLE: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}else if(qs.getStatus() == QuestStatus.START){
			if (targetId == 205936) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1352);
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 0);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205936) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		
		if(targetId == 218215){
			if (var >= 0 && var <= 5) {
				qs.setQuestVarById(0, var + 1);
				updateQuestStatus(env);
				return true;
			}else if(var == 6){
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}