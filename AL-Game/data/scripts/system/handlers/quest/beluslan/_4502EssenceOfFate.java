package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Quest Starter: Hresvelgr (204837). Go to Dark Poeta and find the Balaur Operation Order (730192). Destroy the
 * Telepathy Controller (214894) (1). Destroy power generators to close the Balaur Abyss Gate: Main Power Generator
 * (214895) (1), Auxiliary Power Generator (214896) (1), Emergency Generator (214897) (1). Get rid of Brigade General
 * Anuhart (214904), and take the Concentrated Vitality (182204534) to Heimdall (204182).
 * 
 * @author vlog
 */
public class _4502EssenceOfFate extends QuestHandler {

	private final static int questId = 4502;
	private final static int[] npcs = { 204837, 730192, 204182 };
	private final static int[] mobs = { 214894, 214895, 214896, 214897, 214904 };

	public _4502EssenceOfFate() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204837).addOnQuestStart(questId);
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
			if (targetId == 204837) { // Hresvelgr
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 730192: { // Balaur Operation Orders
					if (var == 0) {
						if (dialog == QuestDialog.USE_OBJECT) {
							return sendQuestDialog(env, 1011);
						}
						else {
							changeQuestStep(env, 0, 1, false); // 1
							return sendQuestDialog(env, 0);
						}
					}
					break;
				}
				case 204182: { // Heimdall
					switch (dialog) {
						case START_DIALOG: {
							if (var == 2) {
								return sendQuestDialog(env, 1352);
							}
						}
						case CHECK_COLLECTED_ITEMS: {
							return checkQuestItems(env, 2, 2, true, 5, 10001); // reward
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204182) {
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
						return true;
					}
					break;
				}
				case 214896: { // Auxiliary Power Generator
					if (var == 2 && var2 != 1) {
						defaultOnKillEvent(env, 214896, 0, 1, 2); // 2: 1
						return true;
					}
					break;
				}
				case 214897: { // Emergency Generator
					if (var == 2 && var3 != 1) {
						defaultOnKillEvent(env, 214897, 0, 1, 3); // 3: 1
						return true;
					}
					break;
				}
			}
		}
		return false;
	}
}