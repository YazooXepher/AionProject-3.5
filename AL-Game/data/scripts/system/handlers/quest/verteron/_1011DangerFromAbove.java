package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke
 */
public class _1011DangerFromAbove extends QuestHandler {

	private final static int questId = 1011;

	public _1011DangerFromAbove() {
		super(questId);
	}

	@Override
	public void register() {
		int[] talkNpcs = { 203109, 203122 };
		qe.registerQuestNpc(700091).addOnKillEvent(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int id : talkNpcs)
			qe.registerQuestNpc(id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var >= 2 && var < 4) {
				return defaultOnKillEvent(env, 700091, 2, 4); // 3, 4
			}
			else if (var == 4) {
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203109) { // Kairon
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
						else if (var == 5) {
							return sendQuestDialog(env, 1693);
						}
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
				}
			}
			else if (targetId == 203122) { // Hynops
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					}
					case STEP_TO_2: {
						playQuestMovie(env, 24);
						return defaultCloseDialog(env, 1, 2); // 2
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203109) { // Kairon
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 1693);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1130, true);
	}
}