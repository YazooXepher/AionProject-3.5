package quest.satra_treasure_hoard;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 *
 */
public class _18902PunishersPosture extends QuestHandler{

	private static final int questId=18902;

	public _18902PunishersPosture() {
		super(questId);
	}


	@Override
	public void register() {
		qe.registerQuestNpc(800332).addOnQuestStart(questId);
		qe.registerQuestNpc(205842).addOnTalkEvent(questId);
		qe.registerQuestNpc(219348).addOnKillEvent(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		switch (targetId) {
			case 219348:
				qs.setQuestVarById(0, var + 1);
				updateQuestStatus(env);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 800332) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs != null &&qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 205842)
			{
				if (env.getDialog() == QuestDialog.START_DIALOG && qs.getQuestVarById(0) == 1)
					return sendQuestDialog(env, 1352);
				else if (env.getDialog() == QuestDialog.SELECT_REWARD)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 5);
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 205842)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}