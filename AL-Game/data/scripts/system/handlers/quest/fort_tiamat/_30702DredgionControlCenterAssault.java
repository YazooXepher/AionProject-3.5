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
public class _30702DredgionControlCenterAssault extends QuestHandler {

	private final static int questId = 30702;
	private final static int npcs [] = {800424, 730702, 800067};

	public _30702DredgionControlCenterAssault() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(800424).addOnQuestStart(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerQuestNpc(219402).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 800424) { 
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
		
    //not use this?
		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 730702) { 
				switch (dialog) {
					case USE_OBJECT: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 800067) { 
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
	  return defaultOnKillEvent(env, 219402, 0, true);
	}
}