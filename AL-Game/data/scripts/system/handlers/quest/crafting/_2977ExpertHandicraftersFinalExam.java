package quest.crafting;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Gigi
 */
public class _2977ExpertHandicraftersFinalExam extends QuestHandler {

	private final static int questId = 2977;

	public _2977ExpertHandicraftersFinalExam() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204108).addOnQuestStart(questId);
		qe.registerQuestNpc(204108).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204108) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204108: {
					switch (env.getDialog()) {
						case START_DIALOG: {
							long itemCount1 = player.getInventory().getItemCountByItemId(182207949);
							if (itemCount1 > 0) {
								removeQuestItem(env, 182207949, 1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 2375);
							}
							else
								return sendQuestDialog(env, 2716);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204108) {
				if (env.getDialogId() == 34)
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}