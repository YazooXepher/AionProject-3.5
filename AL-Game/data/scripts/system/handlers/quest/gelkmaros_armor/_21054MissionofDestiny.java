package quest.gelkmaros_armor;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author keqi
 *
 */
public class _21054MissionofDestiny extends QuestHandler {

	private final static int questId = 21054;

	public _21054MissionofDestiny() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799318).addOnQuestStart(questId);
		qe.registerQuestNpc(799318).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()){
			if (targetId == 799318) {
				switch (dialog) {
					case START_DIALOG:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012: {
						return sendQuestDialog(env, 1012);
					}
					case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					}
					case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					}
					case REFUSE_QUEST: {
						return sendQuestDialog(env, 1004);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799318) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 2375);
					}
					default: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799318) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}