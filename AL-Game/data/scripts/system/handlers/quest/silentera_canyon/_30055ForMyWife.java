package quest.silentera_canyon;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 *
 */
public class _30055ForMyWife extends QuestHandler 
{

	private final static int	questId	= 30055;

	public _30055ForMyWife()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(798929).addOnQuestStart(questId); //Gellius
		qe.registerQuestNpc(798929).addOnTalkEvent(questId); //Gellius
		qe.registerQuestNpc(203901).addOnTalkEvent(questId); //Telemachus
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE){
			if (targetId == 798929){
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798929){
				return sendQuestEndDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203901){
				switch (dialog){
					case START_DIALOG:
						if (var == 0)
							return sendQuestDialog(env, 1352);
					case STEP_TO_1:
						if (var == 0)
							return defaultCloseDialog(env, 0, 1, false, false, 182209222, 1, 0, 0);
				}
			}
			if (targetId == 798929){
				switch (dialog){
					case START_DIALOG:
						if (var == 1)
							return sendQuestDialog(env, 2375);
					case SELECT_REWARD:
						if (var == 1)
							return defaultCloseDialog(env, 1, 2, true, true, 0, 0, 182209222, 1);
				}
			}
		}
		return false;
	}
}