package quest.beshmundir;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Gigi
 */

public class _30227GroupToLiberateSouls extends QuestHandler {

	private final static int questId = 30227;

	public _30227GroupToLiberateSouls() {
		super(questId);
	}

	@Override
	public void register() {
		int[] mobs = { 216586, 216590, 216733, 216734, 216735, 216736, 216737, 216738, 216245 };
		qe.registerQuestNpc(798946).addOnQuestStart(questId);
		qe.registerQuestNpc(798946).addOnTalkEvent(questId);
		qe.registerQuestNpc(799521).addOnTalkEvent(questId);
		qe.registerQuestNpc(799517).addOnTalkEvent(questId);
		for (int mob : mobs)
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		qe.registerOnQuestTimerEnd(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
			
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798946) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}

		if (qs == null)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 799521: {
					switch (dialog) {
						case START_DIALOG: {
							if (qs.getQuestVarById(0) == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_1: 
							return defaultCloseDialog(env, 0, 1);
					}
				}
				case 799517: {
					switch (dialog) {
						case STEP_TO_1: {
							QuestService.questTimerStart(env, 300);
							return true;
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798946) {
				switch (dialog) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 10002);
					}
					case SELECT_REWARD: {
						return sendQuestDialog(env, 5);
					}
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (var == 1) {
				qs.setQuestVarById(0, 0);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() != QuestStatus.START) {
			return false;
		}

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch (targetId) {
			case 216586:
			case 216590:
				if (qs.getQuestVarById(0) == 1) {
					QuestService.questTimerEnd(env);
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					playQuestMovie(env, 445);
					return true;
				}
				break;
			case 216733:
			case 216734:
			case 216735:
			case 216736:
			case 216737:
			case 216738:
			case 216245:
				if (qs.getQuestVarById(0) == 2) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
				break;
		}
		return false;
	}
}