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
public class _2279SolidProof extends QuestHandler
{
	private final static int	questId	= 2279;

	public _2279SolidProof()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(203557).addOnQuestStart(questId); //Suthran
		qe.registerQuestNpc(203557).addOnTalkEvent(questId);
		qe.registerQuestNpc(203590).addOnTalkEvent(questId); //Emgata
		qe.registerQuestNpc(203682).addOnTalkEvent(questId); //Zemurru's Spirit
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
			if(targetId == 203557){//Suthran
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START){
			int var = qs.getQuestVarById(0);
			if (targetId == 203590){//Emgata
				switch (dialog){
					case START_DIALOG:
						if(var == 0)
							return sendQuestDialog(env, 1352);
					case STEP_TO_1:
						if (var == 0){
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							PacketSendUtility
								.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
				}
			}
			if (targetId == 203682){//Zemurru's Spirit
				switch (dialog){
					case START_DIALOG:
						if(var == 1)
							return sendQuestDialog(env, 1693);
					case STEP_TO_2:
						if(var == 1){
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							PacketSendUtility
								.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
				}
			}
			if (targetId == 203557){//Suthran
				switch (dialog){
					case START_DIALOG:
						if(var == 2){
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 2375);
						}
					case SELECT_REWARD:
							return sendQuestEndDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD){
			if (targetId == 203557){//Suthran
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}