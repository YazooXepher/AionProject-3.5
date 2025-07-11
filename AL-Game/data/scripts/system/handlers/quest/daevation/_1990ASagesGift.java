package quest.daevation;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author kecimis,Tiger
 * @reworked vlog
 */
public class _1990ASagesGift extends QuestHandler {

	private final static int questId = 1990;

	// mob counter
	// it's binary. Don't change!
	private int ALL = 0;
	private int A = 0;
	private int B = 0;
	private int C = 0;

	public _1990ASagesGift() {
		super(questId);
	}

	@Override
	public void register() {
		int[] mobs = { 256617, 253721, 253720, 254514, 254513 };
		qe.registerQuestNpc(203771).addOnQuestStart(questId);
		qe.registerQuestNpc(203771).addOnTalkEvent(questId);
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203771) { // Fermina
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					if (isDaevanionArmorEquipped(player)) {
						return sendQuestDialog(env, 4762);
					}
					else {
						return sendQuestDialog(env, 4848);
					}
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			if (targetId == 203771) { // Fermina
				switch (dialog) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
						else if (var == 2 && var1 == 60) {
							return sendQuestDialog(env, 1693);
						}
						else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					}
					case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 0, 1, false, 10000, 10001); // 1
					}
					case SELECT_ACTION_2035: {
						int currentDp = player.getCommonData().getDp();
						int maxDp = player.getGameStats().getMaxDp().getCurrent();
						long burner = player.getInventory().getItemCountByItemId(186000040); // Divine Incense Burner
						if (currentDp == maxDp && burner >= 1) {
							removeQuestItem(env, 186000040, 1);
							player.getCommonData().setDp(0);
							changeQuestStep(env, 3, 3, true); // reward
							return sendQuestDialog(env, 5);
						}
						else {
							return sendQuestDialog(env, 2120);
						}
					}
					case STEP_TO_2: {
						return defaultCloseDialog(env, 1, 2); // 2
					}
					case STEP_TO_3: {
						qs.setQuestVar(3); // 3
						updateQuestStatus(env);
						return sendQuestSelectionDialog(env);
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203771) { // Fermina
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				switch (env.getTargetId()) {
					case 256617: { // Strange Lake Spirit
						if (A >= 0 && A < 30) {
							++A;
							ALL = C;
							ALL = ALL << 7;
							ALL += B;
							ALL = ALL << 7;
							ALL += A;
							ALL = ALL << 7;
							ALL += 2;// var0
							qs.setQuestVar(ALL);
							updateQuestStatus(env);
						}
						break;
					}
					case 253721:
					case 253720: { // Lava Hoverstone
						if (B >= 0 && B < 30) {
							++B;
							ALL = C;
							ALL = ALL << 7;
							ALL += B;
							ALL = ALL << 7;
							ALL += A;
							ALL = ALL << 7;
							ALL += 2;// var0
							qs.setQuestVar(ALL);
							updateQuestStatus(env);
						}
						break;
					}
					case 254514:
					case 254513: { // Disturbed Resident
						if (C >= 0 && C < 30) {
							++C;
							ALL = C;
							ALL = ALL << 7;
							ALL += B;
							ALL = ALL << 7;
							ALL += A;
							ALL = ALL << 7;
							ALL += 2;// var0
							qs.setQuestVar(ALL);
							updateQuestStatus(env);
						}
						break;
					}
				}
				if (qs.getQuestVarById(0) == 2 && A == 30 && B == 30 && C == 30) {
					qs.setQuestVarById(1, 60);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}

	private boolean isDaevanionArmorEquipped(Player player) {
		int plate = player.getEquipment().itemSetPartsEquipped(9);
		int chain = player.getEquipment().itemSetPartsEquipped(8);
		int leather = player.getEquipment().itemSetPartsEquipped(7);
		int cloth = player.getEquipment().itemSetPartsEquipped(6);
		if (plate == 5 || chain == 5 || leather == 5 || cloth == 5) {
			return true;
		}
		return false;
	}
}