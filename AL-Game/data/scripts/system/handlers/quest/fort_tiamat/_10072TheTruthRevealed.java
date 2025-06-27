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
public class _10072TheTruthRevealed extends QuestHandler {

	private final static int questId = 10072;

	public _10072TheTruthRevealed() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 800365, 205535, 205579};
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 205535) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
						else if(player.getInventory().getItemCountByItemId(182213244) >= 3)
							return sendQuestDialog(env, 1352);
						else if(var == 2)
							return sendQuestDialog(env, 1693);
					}
					case CHECK_COLLECTED_ITEMS:{
						return checkQuestItems(env, 1, 2, false, 10000, 10001);
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
					case STEP_TO_2: {
						return sendQuestDialog(env, 1693);
					}
					case STEP_TO_3: {
						//teleport to israphel 800365
						removeQuestItem(env, 182213243, 1);
						return defaultCloseDialog(env, 2, 3);
					}
				}
			}
			else if (targetId == 800365) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					}
					case STEP_TO_4: {
						return defaultCloseDialog(env, 3, 4);
					}
				}
			}
			else if (targetId == 205579) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					}
					case SET_REWARD: {
						return defaultCloseDialog(env, 4, 5, true, false);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205535) { 
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
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10071);
	}
}