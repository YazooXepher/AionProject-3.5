package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Atomics
 */

public class _1367MabangtahsFeast extends QuestHandler {

	private final static int questId = 1367;

	public _1367MabangtahsFeast() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204023).addOnQuestStart(questId);
		qe.registerQuestNpc(204023).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 204023) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.START) {
				long itemCount;
				long itemCount1;
				long itemCount2;
				if (env.getDialog() == QuestDialog.START_DIALOG && qs.getQuestVarById(0) == 0) {
					itemCount = player.getInventory().getItemCountByItemId(182201333); // 2
					itemCount1 = player.getInventory().getItemCountByItemId(182201332); // 5
					itemCount2 = player.getInventory().getItemCountByItemId(182201331); // 1
					if (itemCount > 1 || itemCount1 > 5 || itemCount2 > 0) {
						return sendQuestDialog(env, 1352);
					}
					else {
						return sendQuestDialog(env, 1693);
					}
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					itemCount2 = player.getInventory().getItemCountByItemId(182201331); // 1
					if (itemCount2 > 0) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						qs.setQuestVarById(0, 1);
						return sendQuestDialog(env, 5);
					}
					else
						return sendQuestDialog(env, 1352);
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_2) {
					itemCount1 = player.getInventory().getItemCountByItemId(182201332); // 5
					if (itemCount1 > 4) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						qs.setQuestVarById(0, 2);
						return sendQuestDialog(env, 6);
					}
					else
						return sendQuestDialog(env, 1352);
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					itemCount = player.getInventory().getItemCountByItemId(182201333); // 2
					if (itemCount > 1) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						qs.setQuestVarById(0, 3);
						updateQuestStatus(env);
						return sendQuestDialog(env, 7);
					}
					else
						return sendQuestDialog(env, 1352);
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.REWARD) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}