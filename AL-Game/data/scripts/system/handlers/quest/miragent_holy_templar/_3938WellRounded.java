package quest.miragent_holy_templar;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Nanou, modified by bobobear
 */
public class _3938WellRounded extends QuestHandler {

	private final static int questId = 3938;

	public _3938WellRounded() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203788, 203792, 203790, 203793, 203784, 203786, 798316, 203752, 203701 };
		qe.registerQuestNpc(203701).addOnQuestStart(questId);// Lavirintos
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		// 0 - Start to Lavirintos
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203701) {
				if (dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				// 1 - Talk with Lavirintos and choose a crafting skill
				case 203701:
					if (var == 0) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 1011);
							case STEP_TO_1:
								return defaultCloseDialog(env, 0, 1);
							case STEP_TO_2:
								return defaultCloseDialog(env, 0, 2);
							case STEP_TO_3:
								return defaultCloseDialog(env, 0, 3);
							case STEP_TO_4:
								return defaultCloseDialog(env, 0, 4);
							case STEP_TO_5:
								return defaultCloseDialog(env, 0, 5);
							case STEP_TO_6:
								return defaultCloseDialog(env, 0, 6);
						}
						break;
					}
					// 2 - Talk with Weaponsmithing Master Anteros.
				case 203788:
					if (var == 1) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 1352);
							case STEP_TO_7:
								return defaultCloseDialog(env, 1, 7, 152201596, 1, 0, 0);
						}
					}
					break;
				// 3 - Talk with Handicrafting Master Utsida
				case 203792:
					if (var == 2) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 1693);
							case STEP_TO_7:
								return defaultCloseDialog(env, 2, 7, 152201639, 1, 0, 0);
						}
					}
					break;
				// 4 - Talk with Armorsmithing Master Vulcanus
				case 203790:
					if (var == 3) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 2034);
							case STEP_TO_7:
								return defaultCloseDialog(env, 3, 7, 152201615, 1, 0, 0);
						}
					}
					break;
				// 5 - Talk with Tailoring Master Daphnis
				case 203793:
					if (var == 4) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 2375);
							case STEP_TO_7:
								return defaultCloseDialog(env, 4, 7, 152201632, 1, 0, 0);
						}
					}
					break;
				// 6 - Talk with Cooking Master Hestia
				case 203784:
					if (var == 5) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 2716);
							case STEP_TO_7:
								return defaultCloseDialog(env, 5, 7, 152201644, 1, 0, 0);
						}
					}
					break;
				// 7 - Talk with Alchemy Master Diana
				case 203786:
					if (var == 6) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 3057);
							case STEP_TO_7:
								return defaultCloseDialog(env, 6, 7, 152201643, 1, 0, 0);
						}
					}
					break;
				// 8 - Talk with Crafting Master Anusis
				case 798316:
					if (var == 7) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 3398);
							case CHECK_COLLECTED_ITEMS:
								return checkItemExistence(env, 7, 8, false, 186000077, 1, true, 10000, 10001, 0, 0);
						}
					}
					break;
				// 10 - Take the Glossy Oath Stone to High Priest Jucleas and ask him to perform the ritual of affirmation
				case 203752:
					switch (dialog) {
						case START_DIALOG: {
							if (var == 8) {
								return sendQuestDialog(env, 3739);
							}
						}
						case SET_REWARD: {
							if (player.getInventory().getItemCountByItemId(186000081) >= 1) {
								removeQuestItem(env, 186000081, 1);
								return defaultCloseDialog(env, 8, 8, true, false, 0);
							}
							else {
								return sendQuestDialog(env, 3825);
							}
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				// No match
				default:
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203701) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}