package quest.fenris_fang;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Quest starter: Kvasir (204053). Collect the amulets (600) from Brohum Warriors and Brohum Hunters and take them to
 * Kvasir. Defeat Great Protectors in the Eye of Reshanta (300): Aether's Defender (251002), Fire's Defender (251021),
 * Ancient Defender (251018), Nature's Defender (251039), Light's Defender (251033), Shadow's Defender (251036). Talk
 * with Kvasir. Go to the Dredgion and kill a Dredgion Captains (1): Captain Adhati (214823), Captain Mituna (216850).
 * Talk with Kvasir. Fill yourself with Divine Power and take Mysterious Holy Water (186000086) to High Priest Balder
 * (204075) for the final blessing ritual. Talk with Kvasir.
 * 
 * @author vlog
 */
public class _4944LoyaltyAndAffableness extends QuestHandler {

	private static final int questId = 4944;
	private static final int[] npcs = { 204053, 204075 };
	private static final int[] mobs = { 251002, 251021, 251018, 251039, 251033, 251036, 214823, 216850 };

	public _4944LoyaltyAndAffableness() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204053).addOnQuestStart(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
		if (qs == null) {
			if (targetId == 204053) { // Kvasir
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			switch (targetId) {
				case 204053: { // Kvasir
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 306) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
						}
						case CHECK_COLLECTED_ITEMS: {
							return checkQuestItems(env, 0, 6, false, 10000, 10001); // 6
						}
						case FINISH_DIALOG: {
							return defaultCloseDialog(env, 0, 0);
						}
						case STEP_TO_3: {
							qs.setQuestVar(3); // 3
							updateQuestStatus(env);
							return sendQuestSelectionDialog(env);
						}
						case STEP_TO_5: {
							return defaultCloseDialog(env, 4, 5); // 5
						}
					}
					break;
				}
				case 204075: { // Balder
					switch (dialog) {
						case START_DIALOG: {
							if (var == 5) {
								return sendQuestDialog(env, 2716);
							}
						}
						case SELECT_ACTION_2718: {
							if (player.getCommonData().getDp() >= 4000) {
								return checkItemExistence(env, 5, 5, false, 186000087, 1, true, 2718, 2887, 0, 0);
							}
							else {
								return sendQuestDialog(env, 2802);
							}
						}
						case SET_REWARD: {
							player.getCommonData().setDp(0);
							return defaultCloseDialog(env, 5, 5, true, false); // reward
						}
						case FINISH_DIALOG: {
							return defaultCloseDialog(env, 5, 5);
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204053) { // Kvasir
				if (dialog == QuestDialog.START_DIALOG) {
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
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 6 && var < 306) {
				int[] npcids = { 251002, 251021, 251018, 251039, 251033, 251036 };
				for (int id : npcids) {
					if (targetId == id) {
						qs.setQuestVar(var + 1); // 6 - 306
						updateQuestStatus(env);
						return true;
					}
				}
			}
			else if (var == 3) {
				int[] npcids = { 214823, 216850 };
				return defaultOnKillEvent(env, npcids, 3, 4); // 4
			}
		}
		return false;
	}
}