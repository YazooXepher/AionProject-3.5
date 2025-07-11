package quest.talocs_hollow;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Cheatkiller
 *
 */
public class _11468WithFriendsLikeThese extends QuestHandler {

	private final static int questId = 11468;

	public _11468WithFriendsLikeThese() {
		super(questId);
	}

	public void register() {
		qe.registerQuestSkill(9832, questId);
		qe.registerQuestSkill(9833, questId);
		qe.registerQuestSkill(9834, questId);
		qe.registerQuestNpc(799526).addOnQuestStart(questId);
		qe.registerQuestNpc(799503).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799526) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799503) {
				switch (dialog) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 10002);
					}
					default: {
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		return false;
	}
		
	@Override
	public boolean onUseSkillEvent(QuestEnv env, int skillUsedId) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			int var3 = qs.getQuestVarById(3);
			if(skillUsedId == 9832) {
				if(var1 < 10) {
					qs.setQuestVarById(1, var1 + 1);
					updateQuestStatus(env);
					reward(qs, env);
				}
			}
			else if(skillUsedId == 9833) {
				if(var2 < 5) {
					qs.setQuestVarById(2, var2 + 1);
					updateQuestStatus(env);
					reward(qs, env);
				}
			}
			else if(skillUsedId == 9834) {
				if(var3 < 3) {
					qs.setQuestVarById(3, var3 + 1);
					updateQuestStatus(env);
					reward(qs, env);
				}
			}
		}
		return false;
	}
	
	private void reward(QuestState qs, QuestEnv env) {
		if(qs.getQuestVarById(1) == 10 && qs.getQuestVarById(2) == 5 && qs.getQuestVarById(3) == 3) {
			qs.setQuestVarById(1, 0);
			qs.setQuestVarById(2, 0);
			qs.setQuestVarById(3, 0);
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
		}
	}
}