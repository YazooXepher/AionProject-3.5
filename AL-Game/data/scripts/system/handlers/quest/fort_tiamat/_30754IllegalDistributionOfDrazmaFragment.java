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
public class _30754IllegalDistributionOfDrazmaFragment extends QuestHandler {

	private final static int questId = 30754;
	private final static int npcs [] = {205864, 205889, 205885, 799436};

	public _30754IllegalDistributionOfDrazmaFragment() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205864).addOnQuestStart(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205864) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
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
			if (targetId == 205889) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1);
					}
				}
			}
			else if (targetId == 205885) {
				switch (dialog) {
					case START_DIALOG: {
						if(var == 1)
							return sendQuestDialog(env, 1693);
					}
					case STEP_TO_2: {
						return defaultCloseDialog(env, 1, 2);
						}
					}
				}
			else if (targetId == 799436) {
				switch (dialog) {
					case START_DIALOG: {
						if(var == 2)
							return sendQuestDialog(env, 2034);
					}
					case STEP_TO_3: {
						qs.setQuestVar(3);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
						}
					}
				}
		    }
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205889) { 
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
}