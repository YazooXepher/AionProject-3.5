package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Rolandas
 * @reworked vlog
 */
public class _3965TotheGalleriaofGrandeur extends QuestHandler {

	private final static int questId = 3965;

	public _3965TotheGalleriaofGrandeur() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798311).addOnQuestStart(questId);
		qe.registerQuestNpc(798311).addOnTalkEvent(questId);
		qe.registerQuestNpc(798391).addOnTalkEvent(questId);
		qe.registerQuestNpc(798390).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798311) { // Senarinrinerk
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env, 182206120, 2);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 798391: { // Andu
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1352);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1, 0, 0, 182206120, 1); // 1
						}
					}
					break;
				}
				case 798390: { // Palentine
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SELECT_REWARD: {
							changeQuestStep(env, 1, 1, true);
							removeQuestItem(env, 182206120, 1);
							return sendQuestDialog(env, 5);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798390) { // Palentine
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}