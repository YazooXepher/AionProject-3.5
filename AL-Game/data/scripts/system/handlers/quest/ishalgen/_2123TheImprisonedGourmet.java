package quest.ishalgen;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _2123TheImprisonedGourmet extends QuestHandler {

	private final static int questId = 2123;

	public _2123TheImprisonedGourmet() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203550).addOnQuestStart(questId);
		qe.registerQuestNpc(203550).addOnTalkEvent(questId);
		qe.registerQuestNpc(700128).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203550) { // Munin
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					}
					default: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203550: { // Munin
					switch (dialog) {
						case START_DIALOG: {
							return sendQuestDialog(env, 1352);
						}
						case STEP_TO_1: {
							if (player.getInventory().getItemCountByItemId(182203121) >= 1) {
								qs.setQuestVar(5); // 5
								qs.setStatus(QuestStatus.REWARD); // rewatd
								updateQuestStatus(env);
								removeQuestItem(env, 182203121, 1);
								return sendQuestDialog(env, 5);
							}
							else {
								return sendQuestDialog(env, 1693);
							}
						}
						case STEP_TO_2: {
							if (player.getInventory().getItemCountByItemId(182203122) >= 1) {
								qs.setQuestVar(6); // 6
								qs.setStatus(QuestStatus.REWARD); // rewatd
								updateQuestStatus(env);
								removeQuestItem(env, 182203122, 1);
								return sendQuestDialog(env, 6);
							}
							else {
								return sendQuestDialog(env, 1693);
							}
						}
						case STEP_TO_3: {
							if (player.getInventory().getItemCountByItemId(182203123) >= 1) {
								qs.setQuestVar(7); // 7
								qs.setStatus(QuestStatus.REWARD); // rewatd
								updateQuestStatus(env);
								removeQuestItem(env, 182203123, 1);
								return sendQuestDialog(env, 7);
							}
							else {
								return sendQuestDialog(env, 1693);
							}
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				case 700128: { // Methu Egg
					return true;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203550) { // Munin
				return sendQuestEndDialog(env, qs.getQuestVarById(0) - 5);
			}
		}
		return false;
	}
}