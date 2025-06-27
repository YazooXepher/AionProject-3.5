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
public class _10071OnTheTrailOfIsraphel extends QuestHandler {

	private final static int questId = 10071;

	public _10071OnTheTrailOfIsraphel() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 205579, 205535, 730465, 295987, 730465};
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
			if (targetId == 205579) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
				}
			}
			else if (targetId == 205535) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					}
					case STEP_TO_2: {
						if (giveQuestItem(env, 182213242, 1))
						return defaultCloseDialog(env, 1, 2);
					}
				}
			}
			else if (targetId == 205987) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
						else if (var == 4)
							return sendQuestDialog(env, 2375);
						else if (var == 5)
							return sendQuestDialog(env, 2716);
					}
					case STEP_TO_3: {
						removeQuestItem(env, 182213242, 1);
						return defaultCloseDialog(env, 2, 3);
						}
					case STEP_TO_5: {
						//remove course
						return defaultCloseDialog(env, 4, 5);
						}
					case SET_REWARD: {
						giveQuestItem(env, 182213243, 1);
						return defaultCloseDialog(env, 5, 6, true, false);
					}
				}
			}
			else if (targetId == 730465) { // Mysterious Orb
				switch (dialog) {
					case USE_OBJECT: {
						if (var == 3) {
							//apply course
							changeQuestStep(env, 3, 4, false);
							return true;
						}
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
		return defaultOnLvlUpEvent(env, 10070);
	}
}