package quest.tiamaranta;

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
public class _41517TrippingTheRift extends QuestHandler {

	private final static int questId = 41517;

	public _41517TrippingTheRift() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(205909).addOnQuestStart(questId);
		qe.registerQuestNpc(205914).addOnTalkEvent(questId);
		qe.registerQuestNpc(205938).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205909) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 205914) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1352);
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1);
					}
				}
			}
			else if (targetId == 205938) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 2375);
					}
					case SELECT_REWARD: {
						return defaultCloseDialog(env, 1, 1, true, true);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205938) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 5);
					}
					default: {
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		return false;
	}
}