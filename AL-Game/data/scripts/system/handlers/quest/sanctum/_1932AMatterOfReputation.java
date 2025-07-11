package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 *
 */
public class _1932AMatterOfReputation extends QuestHandler  {

	private final static int	questId	= 1932;
	private final static int[]	npcs = {203893, 203946};
	
	public _1932AMatterOfReputation()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(203893).addOnQuestStart(questId);
		for(int npc: npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
		
		if(sendQuestNoneDialog(env, 203893))
			return true;

		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		
		if(qs.getStatus() == QuestStatus.START)
		{
			switch(targetId)
			{
				case 203946:
					switch(dialog)
					{
						case START_DIALOG:
							if(var == 0)
								return sendQuestDialog(env, 1352);
						case STEP_TO_1:
							return defaultCloseDialog(env, 0, 1);
					}
					break;
				case 203893:
					switch(dialog)
					{
						case START_DIALOG:
							if(var == 1)
								return sendQuestDialog(env, 2375);
						case CHECK_COLLECTED_ITEMS:
							return checkQuestItems(env, 1, 2, true, 5, 2716);
					}
					break;
			}
		}
		return sendQuestRewardDialog(env, 203893, 0);
	}
}