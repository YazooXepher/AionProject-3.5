package quest.altgard;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _2289RampagingMosbears extends QuestHandler {

	private static final int questId = 2289;

	public _2289RampagingMosbears() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203616).addOnQuestStart(questId);
		qe.registerQuestNpc(203616).addOnTalkEvent(questId);
		qe.registerQuestNpc(203618).addOnTalkEvent(questId);
		qe.registerQuestNpc(210564).addOnKillEvent(questId);
		qe.registerQuestNpc(210584).addOnKillEvent(questId);
		qe.registerQuestNpc(210442).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203616) { // Gefion
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 203616: { // Gefion
					switch (dialog) {
						case STEP_TO_1: {
							return sendQuestSelectionDialog(env);
						}
						case START_DIALOG: {
							if (var == 5) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 7) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SELECT_ACTION_1354: {
							playQuestMovie(env, 62);
							return sendQuestDialog(env, 1354);
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 5, 6); // 6
						}
						case CHECK_COLLECTED_ITEMS: {
							return checkQuestItems(env, 7, 7, true, 5, 2120); // reward
						}
						case FINISH_DIALOG: {
							return defaultCloseDialog(env, 7, 7);
						}
					}
					break;
				}
				case 203618: { // Skanin
					switch (dialog) {
						case START_DIALOG: {
							if (var == 6) {
								return sendQuestDialog(env, 1693);
							}
						}
						case STEP_TO_3: {
							return defaultCloseDialog(env, 6, 7, 182203017, 1, 0, 0); // 7
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203616) { // Gefion
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		int[] mobs = { 210564, 210584 };
		return defaultOnKillEvent(env, mobs, 0, 5); // 1, 2, 3, 4, 5
	}
}