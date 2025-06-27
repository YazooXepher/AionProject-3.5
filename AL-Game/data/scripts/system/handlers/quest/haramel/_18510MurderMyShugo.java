package quest.haramel;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _18510MurderMyShugo extends QuestHandler {

	private final static int questId = 18510;

	public _18510MurderMyShugo() {
		super(questId);
	}

	@Override
	public void register() {		
		int[] npcs = { 203166, 700953 };
		qe.registerQuestNpc(203166).addOnQuestStart(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerQuestNpc(700950).addOnKillEvent(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 700950, 0, 3); // Aether Carts: 1, 2, 3
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203166) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env, 182212009, 1);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 700953: {
					if (dialog == QuestDialog.USE_OBJECT) {
						if (var >= 3 && var < 5) {
							changeQuestStep(env, var, var + 1, false); // 4, 5
							return true;
						}
						else if (var == 5) {
							changeQuestStep(env, 5, 5, true); // reward
							return true;
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203166) {
				switch (dialog) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 10002);
					}
					case SELECT_REWARD: {
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