package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Ritsu
 *
 */
public class _1354PraticalAerobatics extends QuestHandler
{
	private final static int	questId	= 1354;
	private String[] rings = { "ERACUS_TEMPLE_210020000_1", "ERACUS_TEMPLE_210020000_2",
		"ERACUS_TEMPLE_210020000_3", "ERACUS_TEMPLE_210020000_4", "ERACUS_TEMPLE_210020000_5",
		"ERACUS_TEMPLE_210020000_6", "ERACUS_TEMPLE_210020000_7"};

	public _1354PraticalAerobatics()
	{
		super(questId);
	}
	@Override
	public void register()
	{
		qe.registerQuestNpc(203983).addOnQuestStart(questId);
		qe.registerQuestNpc(203983).addOnTalkEvent(questId);
		qe.registerOnQuestTimerEnd(questId);
		for (String ring : rings) 
		{
			qe.registerOnPassFlyingRings(ring, questId);
		}
	}

	@Override
	public boolean onPassFlyingRingEvent(QuestEnv env, String flyingRing) 
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (rings[0].equals(flyingRing)) {
				changeQuestStep(env, 1, 2, false); 
				return true;
			}
			else if (rings[1].equals(flyingRing)) {
				changeQuestStep(env, 2, 3, false);
				return true;
			}
			else if (rings[2].equals(flyingRing)) {
				changeQuestStep(env, 3, 4, false); 
				return true;
			}
			else if (rings[3].equals(flyingRing)) {
				changeQuestStep(env, 4, 5, false);
				return true;
			}
			else if (rings[4].equals(flyingRing)) {
				changeQuestStep(env, 5, 6, false);
				return true;
			}
			else if (rings[5].equals(flyingRing)) {
				changeQuestStep(env, 6, 7, false);
				return true;
			}
			else if (rings[6].equals(flyingRing)) {
				qs.setQuestVarById(8, 0);
				changeQuestStep(env, 8, 8, true);
				QuestService.questTimerEnd(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		qs.setQuestVarById(0, 0);
		updateQuestStatus(env);
		return true;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		QuestDialog dialog = env.getDialog();
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE){
			if (targetId == 203983){
				if (dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START){
			if (targetId == 203983){
				switch (dialog){
					case START_DIALOG:
						if (qs.getQuestVarById(0) == 0)
							return sendQuestDialog(env, 1003);
						if (qs.getQuestVarById(0) == 8)
							return sendQuestDialog(env, 2375);
					case STEP_TO_1:
						if (qs.getQuestVarById(0) == 0){
							QuestService.questTimerStart(env, 120);
							return defaultCloseDialog(env, 0, 1);
						}
					case SELECT_REWARD:
						return sendQuestEndDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD){
			if (targetId == 203983)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}