package quest.theobomos;

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
 * @author Manu72
 */
public class _3093RecetteSecretedeQuenelles extends QuestHandler {

	private final static int questId = 3093;

	public _3093RecetteSecretedeQuenelles() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798185).addOnQuestStart(questId); // Bororinerk
		qe.registerQuestNpc(798185).addOnTalkEvent(questId); // Bororinerk
		qe.registerQuestNpc(798177).addOnTalkEvent(questId); // Gastak
		qe.registerQuestNpc(798179).addOnTalkEvent(questId); // Jabala
		qe.registerQuestNpc(203784).addOnTalkEvent(questId); // Hestia
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798185) // Bororinerk
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else if (env.getDialogId() == 1002) {
					if (giveQuestItem(env, 182206062, 1))
						return sendQuestStartDialog(env);
				}

				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) // Reward
		{
			if (env.getDialog() == QuestDialog.START_DIALOG)
				return sendQuestDialog(env, 2375);
			else if (env.getDialogId() == 1009) {
				removeQuestItem(env, 182208052, 1);
				return sendQuestEndDialog(env);
			}
			else
				return sendQuestEndDialog(env);
		}

		else if (targetId == 798177) // Gastak
		{

			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1352);
				else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}

		}

		else if (targetId == 798179) // Jabala
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1693);
				else if (env.getDialog() == QuestDialog.STEP_TO_2) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if (targetId == 203784) // Hestia
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2034);
				else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					if (giveQuestItem(env, 182208052, 1))
						;
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		return false;
	}
}