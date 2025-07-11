package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Talk with Sleipnir (204768). Talk with Rubelik (204743). Talk with Sleipnir. Find Esnu (204808) and make a deal with
 * her. Remove Aika Deathsong (213741) and bring the key (182204312) to Esnu. Talk again with Esnu and get the key to
 * Golden Trumpet Temple (730135). Report the results to Sleipnir.
 * 
 * @author Hellboy aion4Free
 * @reworked vlog
 */
public class _2055TheSeirensTreasure extends QuestHandler {

	private final static int questId = 2055;
	private final static int[] npc_ids = { 204768, 204743, 204808 };

	public _2055TheSeirensTreasure() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 2054);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 2500, 2054 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204768: // Sleipnir
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							if (var == 2)
								return sendQuestDialog(env, 1693);
							if (var == 6 && player.getInventory().getItemCountByItemId(182204321) >= 1)
								return sendQuestDialog(env, 3057);
						case STEP_TO_1:
							if (var == 0) {
								playQuestMovie(env, 239);
								return defaultCloseDialog(env, 0, 1, 182204310, 1, 0, 0); // 1
							}
						case STEP_TO_3:
							return defaultCloseDialog(env, 2, 3); // 3
						case SELECT_REWARD: {
							removeQuestItem(env, 182204321, 1);
							return defaultCloseDialog(env, 6, 6, true, true); // reward
						}
						case SELECT_ACTION_3143:
							return sendQuestDialog(env, 3143);
						case STEP_TO_7: {
							playQuestMovie(env, 239);
							removeQuestItem(env, 182204321, 1);
							return defaultCloseDialog(env, 6, 6, true, true); // reward
						}
					}
					break;
				case 204743: // Rubelik
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 1)
								return sendQuestDialog(env, 1352);
						case STEP_TO_2:
							return defaultCloseDialog(env, 1, 2, 182204311, 1, 182204310, 1); // 2
					}
					break;
				case 204808: // Esnu
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 3)
								return sendQuestDialog(env, 2034);
							if (var == 4)
								return sendQuestDialog(env, 2375);
							if (var == 5)
								return sendQuestDialog(env, 2716);
						case STEP_TO_4:
							if (var == 3) {
								playQuestMovie(env, 240);
								return defaultCloseDialog(env, 3, 4, 0, 0, 182204311, 1); // 4
							}
						case CHECK_COLLECTED_ITEMS:
							return checkQuestItems(env, 4, 5, false, 10000, 10001); // 5
						case FINISH_DIALOG:
							if (var == 5)
								defaultCloseDialog(env, 5, 5); // 5
							if (var == 4)
								defaultCloseDialog(env, 4, 4); // 4
						case STEP_TO_6:
							return defaultCloseDialog(env, 5, 6, false, false, 182204321, 1, 0, 0); // 6
					}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204768) { // Sleipnir
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}