package quest.oriel;

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
public class _18805GoingThrifting extends QuestHandler {
	
	private static final int questId = 18805;

	public _18805GoingThrifting()
	{
		super(questId);
	}

	@Override
	public void register() 
	{
		qe.registerQuestNpc(830070).addOnQuestStart(questId);
		qe.registerQuestNpc(830070).addOnTalkEvent(questId);
		qe.registerQuestNpc(830660).addOnTalkEvent(questId);
		qe.registerQuestNpc(830661).addOnTalkEvent(questId);
		qe.registerQuestNpc(830520).addOnTalkEvent(questId);
		qe.registerQuestNpc(730525).addOnTalkEvent(questId);
		qe.registerQuestNpc(730522).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if(qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if (targetId == 830070) 
			{
				if (dialog == QuestDialog.START_DIALOG) 
					return sendQuestDialog(env, 1011);
				else 
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			int var = qs.getQuestVarById(0);
			switch (targetId)
			{
				case 830660: 
				case 830661: 
				case 830520: 
				{
					switch (dialog)
					{
						case START_DIALOG: 
						{
							if (var == 0) 
								return sendQuestDialog(env, 1352);
							else if (var == 2) 
								return sendQuestDialog(env, 2375);
						}
						case STEP_TO_1:
						{
							return defaultCloseDialog(env, 0, 1);
						}
						case SELECT_REWARD:
						{
							changeQuestStep(env, 2, 2, true);
							return sendQuestDialog(env, 5);
						}
					}
				}
				case 730525: 
				case 730522: 
				{
					switch (dialog)
					{
						case USE_OBJECT: 
						{
							if (var == 1) 
								return sendQuestDialog(env, 1693);
						}
						case STEP_TO_2:
						{
							return defaultCloseDialog(env, 1, 2);
						}
					}
				}

			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) 
		{
			switch (targetId)
			{
				case 830660: 
				case 830661: 
				case 830520: 
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}