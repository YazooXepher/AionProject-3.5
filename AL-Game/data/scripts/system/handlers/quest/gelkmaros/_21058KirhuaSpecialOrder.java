package quest.gelkmaros;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Cheatkiller
 *
 */
public class _21058KirhuaSpecialOrder extends QuestHandler {

	private final static int questId = 21058;

	public _21058KirhuaSpecialOrder() {
		super(questId);
	}

	public void register() {
		qe.registerQuestItem(182207847, questId);
		qe.registerQuestNpc(799360).addOnTalkEvent(questId);
		qe.registerQuestNpc(296489).addOnKillEvent(questId);
		qe.registerQuestNpc(296490).addOnKillEvent(questId);
		qe.registerQuestNpc(296491).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 0) { 
				if (dialog == QuestDialog.ACCEPT_QUEST) {
					removeQuestItem(env, 182207847, 1);
					QuestService.startQuest(env);
					return closeDialogWindow(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799360) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else if (dialog == QuestDialog.SELECT_ACTION_2034) {
					return sendQuestDialog(env, 2034);
				}
				else if (dialog == QuestDialog.SELECT_REWARD) {
					if(player.getInventory().getKinah() >= 20000000) {
						player.getInventory().decreaseKinah(20000000);
						return sendQuestDialog(env, 5);
					}
					else 
						return sendQuestDialog(env, 3739);
				}
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
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 0)
				return defaultOnKillEvent(env, 296489, 0, 1);
			else if(var == 1)
				return defaultOnKillEvent(env, 296490, 1, 2);
			else if(var == 2)
				return defaultOnKillEvent(env, 296491, 2, true);
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
		}
		return HandlerResult.FAILED;
	}
}
