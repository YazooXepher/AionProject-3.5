package quest.theobomos;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Get the antidote (182208035) from Calydon Sorcerer (214304) and bring it to Ruria (798211). Talk with Ruria. Escort
 * Ruria to the place where Melleas (798208) is. Talk with Melleas. Tell Rosina (798190) about Ruria.
 * 
 * @author Balthazar
 * @reworked vlog
 */

public class _3050RescuingRuria extends QuestHandler {

	private final static int questId = 3050;

	public _3050RescuingRuria() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798211).addOnQuestStart(questId);
		qe.registerOnLogOut(questId);
		qe.registerQuestNpc(798211).addOnTalkEvent(questId);
		qe.registerQuestNpc(798208).addOnTalkEvent(questId);
		qe.registerQuestNpc(798190).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798211) { // Ruria
				switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					}
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798211: { // Ruria
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (qs.getQuestVarById(0) == 0) {
								long itemCount = player.getInventory().getItemCountByItemId(182208035);
								if (itemCount >= 1) {
									return sendQuestDialog(env, 1011);
								}
								return sendQuestDialog(env, 1097);
							}
						}
						case USE_OBJECT:
							if (qs.getQuestVarById(0) == 0) {
								return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 798208, 0, 1); // 1
							}
						case SELECT_ACTION_1012: {
							removeQuestItem(env, 182208035, 1);
						}
						case STEP_TO_1: {
							playQuestMovie(env, 370);
							return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 798208, 0, 1); // 1
						}
					}
				}
					break;
				case 798208: { // Melleas
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (qs.getQuestVarById(0) == 2) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SET_REWARD: {
							return defaultCloseDialog(env, 2, 2, true, false);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798190) { // Rosina
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 1) {
				changeQuestStep(env, 1, 0, false);
			}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 1, 2, false); // 2
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 1, 0, false); // 0
	}
}