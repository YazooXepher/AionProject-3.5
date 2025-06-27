package quest.rentus_base;

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
public class _30556TheWholeStory extends QuestHandler
{
	private final static int questId=30556;
	public _30556TheWholeStory()
	{
		super(questId);
	}
	@Override
	public void register()
	{
		qe.registerQuestNpc(799544).addOnQuestStart(questId); 
		qe.registerQuestNpc(799549).addOnTalkEvent(questId); //Maios
		qe.registerQuestNpc(799544).addOnTalkEvent(questId); //Oreitia
	}
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player=env.getPlayer();
		QuestState qs=player.getQuestStateList().getQuestState(questId);
		int targetId=env.getTargetId();
		QuestDialog dialog=env.getDialog();
		if(qs==null || qs.getStatus() ==QuestStatus.NONE || qs.canRepeat())
		{
			if(targetId==799544)
			{
				switch(dialog)
				{
					case START_DIALOG:
						return sendQuestDialog(env,4762);
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if(qs != null && qs.getStatus() == QuestStatus.START)
		{

			switch(targetId)
			{

				case 799544:
				{
					switch(dialog)
					{
						case START_DIALOG:
							return sendQuestDialog(env,1011);
						case CHECK_COLLECTED_ITEMS_SIMPLE:
						{

							return checkQuestItemsSimple(env, 0, 0, false, 10000, 0, 0);
						}
						case STEP_TO_1:
						{
							removeQuestItem(env,182212505,1);
							changeQuestStep(env, 0, 1, true);
							return closeDialogWindow(env);
						}
					}
				}

			}
		}
		else if( qs != null && qs.getStatus() ==QuestStatus.REWARD)
		{

			switch(targetId)
			{
				case 799549:
				{
					switch(dialog)
					{
						case USE_OBJECT:
							return sendQuestDialog(env, 10002);
						case SELECT_REWARD:
							return sendQuestDialog(env, 5);
						default :
							return sendQuestEndDialog(env);
					}

				}
			}
		}
		return false;
	}
}