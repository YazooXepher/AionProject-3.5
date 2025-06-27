package quest.fort_tiamat;

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
public class _30701TheLordOfIllusion extends QuestHandler {

	private final static int questId = 30701;
	private final static int npcs [] = {205842, 800430, 800350};

	public _30701TheLordOfIllusion() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205842).addOnQuestStart(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerQuestNpc(219408).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205842) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 800430) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					}
					case STEP_TO_2: {
						return defaultCloseDialog(env, 1, 2);
					}
				}
			}
			else if (targetId == 800350) {
				switch (dialog) {
					case START_DIALOG: {
						if(var == 2)
							return sendQuestDialog(env, 1693);
					}
					case SET_REWARD: {
						  return defaultCloseDialog(env, 2, 3, true, false);
						}
					}
				}
		    }
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205842) { 
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
	public boolean onKillEvent(QuestEnv env) {
	  return defaultOnKillEvent(env, 219408, 0, 1);
	}
}