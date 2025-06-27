package quest.sarpan;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Cheatkiller
 *
 */
 
public class _41267BlowUp extends QuestHandler {

	private final static int	questId	= 41267;

	public _41267BlowUp() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205762).addOnQuestStart(questId);
		qe.registerQuestNpc(205762).addOnTalkEvent(questId);
		qe.registerQuestItem(182213109, questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205762) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env, 182213109, 1);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 205762) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 1)
						return sendQuestDialog(env, 2375);
				}
				else if (dialog == QuestDialog.SELECT_REWARD) {
					return defaultCloseDialog(env, 1, 1, true, true);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205762) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 5);
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
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, false));
			}
		return HandlerResult.FAILED;
	}
}