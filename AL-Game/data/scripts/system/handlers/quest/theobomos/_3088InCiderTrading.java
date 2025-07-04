package quest.theobomos;

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
public class _3088InCiderTrading extends QuestHandler {

	private final static int questId = 3088;

	public _3088InCiderTrading() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(798202).addOnQuestStart(questId);
		qe.registerQuestNpc(798202).addOnTalkEvent(questId);
		qe.registerQuestNpc(798201).addOnTalkEvent(questId);
		qe.registerQuestNpc(798204).addOnTalkEvent(questId);
		qe.registerQuestNpc(798132).addOnTalkEvent(questId);
		qe.registerQuestNpc(798166).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798202) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798202) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1011);
					}
				}
				else if (dialog == QuestDialog.SELECT_ACTION_1012) {
					if(player.getInventory().getItemCountByItemId(160003020) >= 1)
						return sendQuestDialog(env, 1012);
					else
						return sendQuestDialog(env, 1267);
				}
				else if (dialog == QuestDialog.SELECT_ACTION_1097) {
					if(player.getInventory().getItemCountByItemId(160003020) >= 10)
						return sendQuestDialog(env, 1097);
					else
						return sendQuestDialog(env, 1267);
				}
				else if (dialog == QuestDialog.SELECT_ACTION_1182) {
					if(player.getInventory().getItemCountByItemId(160003020) >= 100)
						return sendQuestDialog(env, 1182);
					else
						return sendQuestDialog(env, 1267);
				}
				else if (dialog == QuestDialog.SELECT_ACTION_1011) {
					return sendQuestDialog(env, 1011);
				}
				else if (dialog == QuestDialog.STEP_TO_1) {
					player.getInventory().decreaseByItemId(160003020, 1);
					return sendQuestDialog(env, 1352);
				}
				else if (dialog == QuestDialog.STEP_TO_2) {
					return defaultCloseDialog(env, 0, 2);
				}
				else if (dialog == QuestDialog.STEP_TO_3) {
					player.getInventory().decreaseByItemId(160003020, 10);
					return sendQuestDialog(env, 2034);
				}
				else if (dialog == QuestDialog.STEP_TO_4) {
					return defaultCloseDialog(env, 0, 4);
				}
				else if (dialog == QuestDialog.STEP_TO_7) {
					player.getInventory().decreaseByItemId(160003020, 100);
					return sendQuestDialog(env, 3398);
				}
				else if (dialog == QuestDialog.STEP_TO_8) {
					return defaultCloseDialog(env, 0, 8);
				}
			}
			if (targetId == 798201) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 2) {
						return sendQuestDialog(env, 1693);
					}
					if(qs.getQuestVarById(0) == 5) {
						return sendQuestDialog(env, 2716);
					}
				}
				else if (dialog == QuestDialog.STEP_TO_6) {
					return defaultCloseDialog(env, 5, 6);
				}
				else if (dialog == QuestDialog.SELECT_REWARD) {
					return defaultCloseDialog(env, 2, 2, true, true);
				}
			}
			if (targetId == 798204) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 4) {
						return sendQuestDialog(env, 2375);
					}
				}
				else if (dialog == QuestDialog.STEP_TO_5) {
					return defaultCloseDialog(env, 4, 5);
				}
			}
			if (targetId == 798132) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 6) {
						return sendQuestDialog(env, 3057);
					}
				}
				else if (dialog == QuestDialog.SELECT_REWARD) {
					return defaultCloseDialog(env, 6, 6, true, true);
				}
			}
			if (targetId == 798166) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 8) {
						if(player.getInventory().getItemCountByItemId(182208064) >= 1)
							return sendQuestDialog(env, 3739);
					}
				}
				else if (dialog == QuestDialog.SELECT_REWARD) {
					player.getInventory().decreaseByItemId(182208064, 1);
					return defaultCloseDialog(env, 8, 8, true, true);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798201 && qs.getQuestVarById(0) == 2) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env);
			}
			else if (targetId == 798132 && qs.getQuestVarById(0) == 6) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env, 1);
			}
			else if (targetId == 798166 && qs.getQuestVarById(0) == 8) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env, 2);
			}
		}
		return false;
	}
}