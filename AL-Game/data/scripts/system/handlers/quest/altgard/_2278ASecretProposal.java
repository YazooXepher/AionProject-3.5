package quest.altgard;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ritsu
 * 
 */
public class _2278ASecretProposal extends QuestHandler
{
	private final static int	questId	= 2278;

	public _2278ASecretProposal()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(203590).addOnQuestStart(questId); //Emgata
		qe.registerQuestNpc(203590).addOnTalkEvent(questId);
		qe.registerQuestNpc(203557).addOnTalkEvent(questId); //Suthran
		qe.registerQuestNpc(204206).addOnTalkEvent(questId); //Cavalorn
		qe.registerQuestNpc(204075).addOnTalkEvent(questId); //Balder
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);	
		QuestDialog dialog = env.getDialog();
		
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE){
			if(targetId == 203590){
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START){
			int var = qs.getQuestVarById(0);
			if (targetId == 203557){//Suthran
				switch (dialog){
					case START_DIALOG:
						if (var == 1)
							return sendQuestDialog(env, 1352);
						if (var == 3){
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 2375);
						}
					case STEP_TO_1:
						if (var == 0){
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							PacketSendUtility
								.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					case SELECT_REWARD:
						return sendQuestEndDialog(env);
				}
			}
			if (targetId == 204206){//Cavalorn
				switch (dialog){
					case START_DIALOG:
						if (var == 1)
							return sendQuestDialog(env, 1693);
					case STEP_TO_2:
						if (var == 1){
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							PacketSendUtility
								.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
				}
			}
			if (targetId == 204075){//Balder
				switch (dialog){
					case START_DIALOG:
						if (var == 2)
							return sendQuestDialog(env, 2034);
					case STEP_TO_3:
						if (var == 2){
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							PacketSendUtility
								.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD){
			if (targetId == 203557){
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}