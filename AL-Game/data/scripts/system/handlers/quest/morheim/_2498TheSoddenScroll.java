package quest.morheim;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
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
public class _2498TheSoddenScroll extends QuestHandler {

	private final static int questId = 2498;
	
	
	public _2498TheSoddenScroll() {
		super(questId);
	}

	public void register() {
		qe.registerQuestItem(182204232, questId);
		qe.registerQuestNpc(798125).addOnTalkEvent(questId);
		qe.registerQuestNpc(700302).addOnTalkEvent(questId);
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
					QuestService.startQuest(env);
					return closeDialogWindow(env);
				}
			}
			else if (targetId == 700302) {
				Npc npc = (Npc) env.getVisibleObject();
				giveQuestItem(env, 182204232, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				return true;
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798125) { 
				if (dialog == QuestDialog.START_DIALOG) {
						return sendQuestDialog(env, 2375);
				}
			  else if (dialog == QuestDialog.SELECT_REWARD) {
			  	removeQuestItem(env, 182204232, 1);
			  	return defaultCloseDialog(env, 0, 1, true, true);
			}
		}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798125) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				}
				return sendQuestEndDialog(env);
			}
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