package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * Quesr starter Maloren (204656). Go to Dark Poeta and find the Balaur Operation Orders (730192). Destroy the Telepathy
 * Controller (214894) (1). Destroy the power generators to close the Balaur Abyss Gate. Main Power Generator (214895)
 * (1). Auxiliary Power Generator (214896) (1). Emergency Generator (214897) (1). Kill Brigade General Anuhart (214904)
 * (1). Return to Sanctum and report to Jucleas (203752).
 * 
 * @author vlog
 */
public class _3502NereusNeedsYou extends QuestHandler {

	private final static int questId = 3502;
	private final static int[] npcs = { 204656, 203752, 730192 };
	private final static int[] mobs = { 214894, 214895, 214896, 214897, 214904 };

	public _3502NereusNeedsYou() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204656).addOnQuestStart(questId);
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
		Npc npc = (Npc) env.getVisibleObject();
		int targetId = npc.getNpcId();
		QuestDialog dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204656) { // Maloren
				if (dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 730192: { // Balaur Operation Orders
					if (dialog == QuestDialog.USE_OBJECT && var == 0) {
						return sendQuestDialog(env, 1011);
					}
					if (dialog == QuestDialog.STEP_TO_1)
						return defaultCloseDialog(env, 0, 1); // 1
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203752) {
				if (dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		Npc npc = (Npc) env.getVisibleObject();
		int targetId = npc.getNpcId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			int var3 = qs.getQuestVarById(3);

			switch (targetId) {
				case 214894: { // Telepathy Controller
					if (var == 1)
						return defaultOnKillEvent(env, 214894, 1, 2, 0); // 2
					break;
				}
				case 214895: { // Main Power Generator
					if (var == 2 && var1 != 1) {
						defaultOnKillEvent(env, 214895, 0, 1, 1); // 1: 1
						if (var2 == 1 && var3 == 1) {
							QuestService.addNewSpawn(300040000, player.getInstanceId(), 214904, 275.34537f, 323.02072f,
								(float) 130.9302f, (byte) 52);
							return true;
						}
						return true;
					}
					break;
				}
				case 214896: { // Auxiliary Power Generator
					if (var == 2 && var2 != 1) {
						defaultOnKillEvent(env, 214896, 0, 1, 2); // 2: 1
						if (var1 == 1 && var3 == 1) {
							QuestService.addNewSpawn(300040000, player.getInstanceId(), 214904, 275.34537f, 323.02072f,
								(float) 130.9302f, (byte) 52);
							return true;
						}
						return true;
					}
					break;
				}
				case 214897: { // Emergency Generator
					if (var == 2 && var3 != 1) {
						defaultOnKillEvent(env, 214897, 0, 1, 3); // 3: 1
						if (var1 == 1 && var2 == 1) {
							QuestService.addNewSpawn(300040000, player.getInstanceId(), 214904, 275.34537f, 323.02072f,
								(float) 130.9302f, (byte) 52);
							return true;
						}
						return true;
					}
					break;
				}
				case 214904: { // Brigade General Anuhart
					if (var == 2 && var1 == 1 && var2 == 1 && var3 == 1) {
						return defaultOnKillEvent(env, 214904, 2, true); // reward
					}
					break;
				}
			}
		}
		return false;
	}
}